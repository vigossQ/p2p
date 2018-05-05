package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.domain.Account;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.domain.*;
import cn.wolfcode.p2p.business.mapper.PaymentScheduleMapper;
import cn.wolfcode.p2p.business.service.*;
import cn.wolfcode.p2p.business.utils.CalculatetUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentScheduleServiceImpl implements IPaymentScheduleService {

    @Autowired
    private PaymentScheduleMapper paymentScheduleMapper;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IAccountFlowService accountFlowService;
    @Autowired
    private ISystemAccountService systemAccountService;
    @Autowired
    private ISystemAccountFlowService systemAccountFlowService;
    @Autowired
    private IBidService bidService;
    @Autowired
    private IBidRequestService bidRequestService;
    @Autowired
    private IPaymentScheduleDetailService paymentScheduleDetailService;
    @Autowired
    private IExpAccountService expAccountService;
    @Autowired
    private IExpAccountFlowService expAccountFlowService;

    @Override
    public int save(PaymentSchedule paymentSchedule) {
        return paymentScheduleMapper.insert(paymentSchedule);
    }

    @Override
    public int update(PaymentSchedule paymentSchedule) {
        return paymentScheduleMapper.updateByPrimaryKey(paymentSchedule);
    }

    @Override
    public PaymentSchedule get(Long id) {
        return paymentScheduleMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<PaymentSchedule> list = paymentScheduleMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public void returnMoney(Long id) {
        //判断是否处于待还,用户可用金额>=该期还款金额,当前用户==应还款用户
        PaymentSchedule ps = this.get(id);
        Account account = accountService.getCurrent();
        SystemAccount currentSystemAccount = systemAccountService.getCurrent();
        if (ps != null && ps.getState() == BidConst.PAYMENT_STATE_NORMAL &&
                ((ps.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL &&
                        ps.getBorrowUserId().equals(UserContext.getCurrent().getId()) &&
                        account.getUsableAmount().compareTo(ps.getTotalAmount()) >= 0)
                        ||
                        (ps.getBidRequestType() == BidConst.BIDREQUEST_TYPE_EXP &&
                                currentSystemAccount.getUsableAmount().compareTo(ps.getInterest()) >= 0))
                ) {
            //还款对象
            ps.setPayDate(new Date());
            ps.setState(BidConst.PAYMENT_STATE_DONE);
            //批量修改还款明细对象日期
            paymentScheduleDetailService.updatePayDate(ps.getId(), ps.getPayDate());
            //借款人
            if (ps.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                //可用金额减少,待还本息减少,剩余授信额度增加
                account.setUsableAmount(account.getUsableAmount().subtract(ps.getTotalAmount()));
                account.setUnReturnAmount(account.getUnReturnAmount().subtract(ps.getTotalAmount()));
                //剩余授信额度=原剩余授信额度+该期还款的本金
                account.setRemainBorrowLimit(account.getRemainBorrowLimit().add(ps.getPrincipal()));
                accountService.update(account);//更新数据库
                //生成还款成功流水
                accountFlowService.createPayReturnMoneyFlow(account, ps.getTotalAmount());
            }
            //投资人
            Map<Long, Account> accountMap = new HashMap<>();
            Map<Long, ExpAccount> expAccountMap = new HashMap<>();
            Account bidUserAccount;
            ExpAccount expAccount;
            Long bidUserAccountId;
            SystemAccount systemAccount = systemAccountService.getCurrent();
            for (PaymentScheduleDetail psd : ps.getDetails()) {
                bidUserAccountId = psd.getInvestorId();
                bidUserAccount = accountMap.get(bidUserAccountId);
                expAccount = expAccountMap.get(bidUserAccountId);
                if (bidUserAccount == null) {
                    bidUserAccount = accountService.get(bidUserAccountId);
                    accountMap.put(bidUserAccountId, bidUserAccount);
                    expAccount = expAccountService.get(bidUserAccountId);
                    expAccountMap.put(bidUserAccountId, expAccount);
                }
                if (ps.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {

                    //可用金额增加,待收本金减少,待收利息减少
                    //可用金额=原可用金额+该期还款明细中的还款金额
                    bidUserAccount.setUsableAmount(bidUserAccount.getUsableAmount().add(psd.getTotalAmount()));
                    //待收本金=原待收本金-该期还款明细中的还款本金
                    bidUserAccount.setUnReceivePrincipal(bidUserAccount.getUnReceivePrincipal().subtract(psd.getPrincipal()));
                } else {
                    //体验标:体验金账户余额增加
                    expAccount.setUsableAmount(expAccount.getUsableAmount().add(psd.getPrincipal()));
                    expAccountFlowService.creatReceiveFlow(expAccount, psd.getPrincipal());
                    //系统账户支付利息
                    systemAccountService.payInterest(psd);
                    //收取利息
                    bidUserAccount.setUsableAmount(bidUserAccount.getUsableAmount().add(psd.getInterest()));
                    accountFlowService.createReceiveInterestMoneyFlow(bidUserAccount, psd.getInterest());
                }
                //待收利息=原待收利息-该期还款明细中的还款利息
                bidUserAccount.setUnReceiveInterest(bidUserAccount.getUnReceiveInterest().subtract(psd.getInterest()));
                //生成流水
                accountFlowService.createGainReturnMoneyFlow(bidUserAccount, psd.getTotalAmount());
                //支付信息管理费
                BigDecimal interestManagerCharge = CalculatetUtil.calInterestManagerCharge(psd.getInterest());
                //原可用金额-利息管理费
                bidUserAccount.setUsableAmount(bidUserAccount.getUsableAmount().subtract(interestManagerCharge));
                //生成流水
                accountFlowService.createPayInterestManagerChargeFlow(bidUserAccount, interestManagerCharge);
                //系统账户收取信息管理费
                systemAccount.setUsableAmount(systemAccount.getUsableAmount().add(interestManagerCharge));
                //生成流水
                systemAccountFlowService.createGainInterestManagerChargeFlow(systemAccount, interestManagerCharge);
            }
            //进行统一的账户更新
            for (Account accountTemp : accountMap.values()) {
                accountService.update(accountTemp);
            }
            for (ExpAccount expAccount1 : expAccountMap.values()) {
                expAccountService.update(expAccount1);
            }
            systemAccountService.update(systemAccount);
            this.update(ps);
            //判断是否已经还清
            List<PaymentSchedule> paymentScheduleList = paymentScheduleMapper.queryByBidRequestId(ps.getBidRequestId());
            for (PaymentSchedule paymentSchedule : paymentScheduleList) {
                if (paymentSchedule.getState() != BidConst.PAYMENT_STATE_DONE) {
                    //存在一条记录是没有还清的,就不做操作
                    return;
                }
            }
            //如果还清
            //需要修改借款对象和投标对象状态--->已还清
            BidRequest bidRequest = bidRequestService.get(ps.getBidRequestId());
            bidRequest.setBidRequestState(BidConst.BIDREQUEST_STATE_COMPLETE_PAY_BACK);
            bidRequestService.update(bidRequest);
            bidService.updateState(bidRequest.getId(), BidConst.BIDREQUEST_STATE_COMPLETE_PAY_BACK);
        }
    }
}
