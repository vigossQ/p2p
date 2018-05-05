package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.domain.Account;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.BitStatesUtils;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.domain.*;
import cn.wolfcode.p2p.business.mapper.BidRequestMapper;
import cn.wolfcode.p2p.business.query.BidRequestQueryObject;
import cn.wolfcode.p2p.business.service.*;
import cn.wolfcode.p2p.business.utils.CalculatetUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional
public class BidRequestServiceImpl implements IBidRequestService {

    @Autowired
    private BidRequestMapper bidRequestMapper;
    @Autowired
    private IUserinfoService userinfoService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IBidRequestAuditHistoryService bidRequestAuditHistoryService;
    @Autowired
    private IBidService bidService;
    @Autowired
    private IAccountFlowService accountFlowService;
    @Autowired
    private ISystemAccountService systemAccountService;
    @Autowired
    private ISystemAccountFlowService systemAccountFlowService;
    @Autowired
    private IPaymentScheduleService paymentScheduleService;
    @Autowired
    private IPaymentScheduleDetailService paymentScheduleDetailService;
    @Autowired
    private IExpAccountService expAccountService;
    @Autowired
    private IExpAccountFlowService expAccountFlowService;

    @Override
    public int save(BidRequest bidRequest) {
        return bidRequestMapper.insert(bidRequest);
    }

    @Override
    public int update(BidRequest bidRequest) {
        int count = bidRequestMapper.updateByPrimaryKey(bidRequest);
        if (count == 0) {
            throw new RuntimeException("乐观锁异常");
        }
        return count;
    }

    @Override
    public BidRequest get(Long id) {
        return bidRequestMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<BidRequest> list = bidRequestMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public boolean canApplyBorrow(Userinfo userinfo) {
        if (userinfo.getIsBasicInfo() &&
                userinfo.getIsRealAuth() &&
                userinfo.getIsVedioAuth() &&
                userinfo.getScore() >= BidConst.CREDIT_BORROW_SCORE) {
            return true;
        }
        return false;
    }

    @Override
    public void apply(BidRequest bidRequest) {
        Userinfo userinfo = userinfoService.getCurrent();
        Account account = accountService.getCurrent();
        //判断用户是否有借款资格
        //判断当前用户的状态是否是已经发起借款
        //借款金额应该在范围内
        //借款利息应该在范围内
        //最小投标条件
        if (this.canApplyBorrow(userinfo) &&//用户是否有借款资格
                !userinfo.getHasBidRequestProcess() &&//当前用户的状态是否是已经发起借款
                bidRequest.getBidRequestAmount().compareTo(BidConst.SMALLEST_BIDREQUEST_AMOUNT) >= 0 &&//借款金额>=系统规定最小借款金额
                bidRequest.getBidRequestAmount().compareTo(account.getRemainBorrowLimit()) <= 0 &&//借款金额<=账户剩余授信额度
                bidRequest.getCurrentRate().compareTo(BidConst.SMALLEST_CURRENT_RATE) >= 0 &&//该次借款利息>=系统设定最小借款利息
                bidRequest.getCurrentRate().compareTo(BidConst.MAX_CURRENT_RATE) <= 0 &&//该次借款利息<=系统设定最大借款利息
                bidRequest.getMinBidAmount().compareTo(BidConst.SMALLEST_BID_AMOUNT) >= 0//该次最小投标>=系统设定最小投标
                ) {
            BidRequest br = new BidRequest();
            br.setApplyTime(new Date());//借款时间
            br.setBidRequestAmount(bidRequest.getBidRequestAmount());//借款金额
            br.setBidRequestState(BidConst.BIDREQUEST_STATE_PUBLISH_PENDING);//设置借款状态:待发布
            br.setBidRequestType(BidConst.BIDREQUEST_TYPE_NORMAL);//设置借款类型:普通信用贷
            br.setCreateUser(UserContext.getCurrent());//借款人
            br.setCurrentRate(bidRequest.getCurrentRate());//借款利率
            br.setDescription(bidRequest.getDescription());//借款描述
            br.setDisableDays(bidRequest.getDisableDays());//招标天数
            br.setMinBidAmount(bidRequest.getMinBidAmount());//最小招标金额
            br.setMonthes2Return(bidRequest.getMonthes2Return());//借款的日期
            br.setReturnType(BidConst.RETURN_TYPE_MONTH_INTEREST_PRINCIPAL);//还款方式:按月分期
            br.setTitle(bidRequest.getTitle());//借款标题
            //该次借款的总利息
            br.setTotalRewardAmount(CalculatetUtil.calTotalInterest(bidRequest.getReturnType(), bidRequest.getBidRequestAmount(), bidRequest.getCurrentRate(), bidRequest.getMonthes2Return()));
            this.save(br);
            //借款人修改状态,发起借款
            userinfo.addState(BitStatesUtils.OP_HAS_BIDREQUEST_PROCESS);
            userinfoService.update(userinfo);
        }
    }

    @Override
    public void publisAudit(Long id, int state, String remark) {
        BidRequest bidRequest = this.get(id);
        if (bidRequest != null && bidRequest.getBidRequestState() == BidConst.BIDREQUEST_STATE_PUBLISH_PENDING) {
            //审核历史对象
            BidRequestAuditHistory brah = new BidRequestAuditHistory();
            brah.setAuditType(BidRequestAuditHistory.PUBLISH_AUDIT);//发标前审核
            brah.setBidRequestId(bidRequest.getId());//关联借款对象id
            brah.setApplier(bidRequest.getCreateUser());//借款申请人(借款人)
            brah.setApplyTime(bidRequest.getApplyTime());//申请时间
            brah.setAuditor(UserContext.getCurrent());//审核人
            brah.setAuditTime(new Date());//审核时间
            brah.setRemark(remark);
            if (state == BidRequestAuditHistory.STATE_PASS) {
                //审核通过
                brah.setState(BidRequestAuditHistory.STATE_PASS);
                //状态变为招标中
                //设置借款的发布时间,截止时间,风控意见
                bidRequest.setBidRequestState(BidConst.BIDREQUEST_STATE_BIDDING);
                bidRequest.setPublishTime(new Date());//发布时间
                //截止时间=发布时间+招标天数
                bidRequest.setDisableDate(DateUtils.addDays(bidRequest.getPublishTime(), bidRequest.getDisableDays()));
                bidRequest.setNote(remark);
            } else {
                //审核拒绝
                brah.setState(BidRequestAuditHistory.STATE_REJECT);
                bidRequest.setBidRequestState(BidConst.BIDREQUEST_STATE_PUBLISH_REFUSE);//发标审核拒绝
                bidRequest.setNote(remark);
                if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                    //移除借款人借款状态
                    Userinfo applierUserinfo = userinfoService.get(bidRequest.getCreateUser().getId());
                    applierUserinfo.removeState(BitStatesUtils.OP_HAS_BIDREQUEST_PROCESS);
                    userinfoService.update(applierUserinfo);
                }
            }
            this.update(bidRequest);
            bidRequestAuditHistoryService.save(brah);
        }
    }

    @Override
    public List<BidRequest> queryIndexData(BidRequestQueryObject qo, int bidRequestType) {
        PageHelper.startPage(0, 5);
        qo.setBidRequestType(bidRequestType);
        return bidRequestMapper.selectList(qo);
    }

    @Override
    public void bid(Long bidRequestId, BigDecimal amount) {
        BidRequest bidRequest = this.get(bidRequestId);//借款对象
        Account account = accountService.getCurrent();//当前账户
        ExpAccount expAccount = expAccountService.get(account.getId());
        //判断当前借款在招标中
        if (bidRequest != null &&
                bidRequest.getBidRequestState() == BidConst.BIDREQUEST_STATE_BIDDING &&//处于招标中状态
                amount.compareTo(bidRequest.getMinBidAmount()) >= 0 &&//投标金额>=借款设定最小金额
                (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL &&
                        (bidRequest.getCreateUser().getId() != UserContext.getCurrent().getId() &&//当前用户!=借款人
                                amount.compareTo(account.getUsableAmount().min(bidRequest.getRemainAmount())) <= 0)//投标金额<=MIN(账户可用余额,该标还剩余的金额)
                        ||
                        (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_EXP
                                && amount.compareTo(expAccount.getUsableAmount().min(bidRequest.getRemainAmount())) <= 0//投标金额<=MIN(账户体验金余额,该标还剩余的金额)
                        ))
                ) {
            //创建投标对象Bid
            Bid bid = new Bid();
            bid.setActualRate(bidRequest.getCurrentRate());//借款利率
            bid.setAvailableAmount(amount);//投标金额
            bid.setBidRequestId(bidRequestId);//关联借款id
            bid.setBidRequestState(bidRequest.getBidRequestState());//设置投标状态
            bid.setBidRequestTitle(bidRequest.getTitle());//借款标题
            bid.setBidTime(new Date());//投标时间
            bid.setBidUser(UserContext.getCurrent());//投标用户
            bidService.save(bid);
            //投资人的属性变化
            if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                //可用金额减少,冻结金额增加(信用标)
                account.setUsableAmount(account.getUsableAmount().subtract(amount));
                account.setFreezedAmount(account.getFreezedAmount().add(amount));
                accountService.update(account);//执行更新
                //添加投标流水
                accountFlowService.createBidFlow(account, amount);
            } else {
                //体验标
                expAccount.setUsableAmount(expAccount.getUsableAmount().subtract(amount));
                expAccount.setFreezedAmount(expAccount.getFreezedAmount().add(amount));
                expAccountService.update(expAccount);
                //增加一个体验金流水
                expAccountFlowService.createBidFlow(expAccount, amount);
            }

            //借款对象的属性变化
            bidRequest.setBidCount(bidRequest.getBidCount() + 1);
            bidRequest.setCurrentSum(bidRequest.getCurrentSum().add(amount));

            //投满之后应该改变借款对象和投标对象的状态
            if (bidRequest.getCurrentSum().compareTo(bidRequest.getBidRequestAmount()) == 0) {
                int state = bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_EXP ?
                        BidConst.BIDREQUEST_STATE_APPROVE_PENDING_2 : BidConst.BIDREQUEST_STATE_APPROVE_PENDING_1;
                bidRequest.setBidRequestState(state);
                //修改投标对象状态
                bidService.updateState(bidRequest.getId(), state);
            }
            this.update(bidRequest);
        }
    }

    @Override
    public void audit1(Long id, int state, String remark) {
        BidRequest bidRequest = this.get(id);
        //判断借款是否处于满标一审
        if (bidRequest != null && bidRequest.getBidRequestState() == BidConst.BIDREQUEST_STATE_APPROVE_PENDING_1) {
            //需要创建审核历史对象
            creatBidRequestAuditHistory(bidRequest, state, BidConst.BIDREQUEST_STATE_APPROVE_PENDING_1, remark);
            if (state == BidRequestAuditHistory.STATE_PASS) {
                //如果审核通过
                //修改借款对象状态和投标对象状态---->满标二审
                bidRequest.setBidRequestState(BidConst.BIDREQUEST_STATE_APPROVE_PENDING_2);
                bidService.updateState(bidRequest.getId(), BidConst.BIDREQUEST_STATE_APPROVE_PENDING_2);
            } else {
                auditReject(bidRequest);
            }
            this.update(bidRequest);
        }
    }

    @Override
    public void audit2(Long id, int state, String remark) {
        //判断是否处于满标二审状态
        BidRequest bidRequest = this.get(id);
        if (bidRequest != null && bidRequest.getBidRequestState() == BidConst.BIDREQUEST_STATE_APPROVE_PENDING_2) {
            //审核历史
            creatBidRequestAuditHistory(bidRequest, state, BidConst.BIDREQUEST_STATE_APPROVE_PENDING_2, remark);
            //如果审核通过
            if (state == BidRequestAuditHistory.STATE_PASS) {
                //借款对象
                //状态:还款中
                bidRequest.setBidRequestState(BidConst.BIDREQUEST_STATE_PAYING_BACK);
                //投标对象
                //批量状态:还款中
                bidService.updateState(bidRequest.getId(), BidConst.BIDREQUEST_STATE_PAYING_BACK);
                if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                    //借款人账户
                    Account creatUserAccount = accountService.get(bidRequest.getCreateUser().getId());
                    //增加可用金额,待还本息,减少授信额度
                    //用户可用金额=原可用金额+该次借款金额
                    creatUserAccount.setUsableAmount(creatUserAccount.getUsableAmount().add(bidRequest.getBidRequestAmount()));
                    //用户的待还本息=原待还本息+该次借款金额+该次贷款利息
                    creatUserAccount.setUnReturnAmount(creatUserAccount.getUnReturnAmount().add(bidRequest.getBidRequestAmount().add(bidRequest.getTotalRewardAmount())));
                    //用户剩余授信额度=原授信额度-该次贷款金额
                    creatUserAccount.setRemainBorrowLimit(creatUserAccount.getRemainBorrowLimit().subtract(bidRequest.getBidRequestAmount()));
                    //生成借款成功流水
                    accountFlowService.createBorrowFlow(creatUserAccount, bidRequest.getBidRequestAmount());
                    //借款人支付平台借款手续费,可用金额减少
                    BigDecimal accountManagementCharge = CalculatetUtil.calAccountManagementCharge(bidRequest.getBidRequestAmount());
                    creatUserAccount.setUsableAmount(creatUserAccount.getUsableAmount().subtract(accountManagementCharge));
                    //生成支付借款收费流水
                    accountFlowService.createPayAccountManagementCharge(creatUserAccount, accountManagementCharge);
                    accountService.update(creatUserAccount);//更新数据库
                    //系统账户收取借款手续费,增加可用金额(手续费)
                    SystemAccount systemAccount = systemAccountService.getCurrent();
                    systemAccount.setUsableAmount(systemAccount.getUsableAmount().add(accountManagementCharge));
                    systemAccountService.update(systemAccount);
                    //生成收取借款收费流水
                    systemAccountFlowService.createGainAccountManagementCharge(systemAccount, accountManagementCharge);
                    //借款人移除借款中的状态码
                    Userinfo createUserinfo = userinfoService.get(bidRequest.getCreateUser().getId());
                    createUserinfo.removeState(BitStatesUtils.OP_HAS_BIDREQUEST_PROCESS);
                    userinfoService.update(createUserinfo);
                }

                //投资人账户
                Map<Long, Account> accountMap = new HashMap<>();
                Map<Long, ExpAccount> expAccountMap = new HashMap<>();
                Account bidUserAccount;
                ExpAccount expAccount;
                Long bidUserId;
                //遍历投标的集合
                for (Bid bid : bidRequest.getBids()) {
                    bidUserId = bid.getBidUser().getId();
                    if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                        bidUserAccount = accountMap.get(bidUserId);
                        if (bidUserAccount == null) {
                            bidUserAccount = accountService.get(bidUserId);
                            accountMap.put(bidUserId, bidUserAccount);
                        }
                        //投资人冻结金额减少,待收本金增加,待收利息增加
                        //投资人冻结金额=原冻结金额-投标金额
                        bidUserAccount.setFreezedAmount(bidUserAccount.getFreezedAmount().subtract(
                                bid.getAvailableAmount()));
                        //待收本金,待收利息(TODO)--->应该在还款对象中计算
                        accountFlowService.createBidSuccessFlow(bidUserAccount, bid.getAvailableAmount());
                    } else {
                        expAccount = expAccountMap.get(bidUserId);
                        accountMap.put(bidUserId,accountService.get(bidUserId));
                        if (expAccount == null) {
                            expAccount = expAccountService.get(bidUserId);
                            expAccountMap.put(bidUserId, expAccount);
                        }
                        expAccount.setFreezedAmount(expAccount.getFreezedAmount().subtract(
                                bid.getAvailableAmount()));
                        expAccountFlowService.creatBidSuccessFlow(expAccount, bid.getAvailableAmount());
                    }
                }
                //还款对象
                List<PaymentSchedule> paymentScheduleList = creatPaymentScheduleList(bidRequest);
                Account accountTemp;
                for (PaymentSchedule ps : paymentScheduleList) {
                    for (PaymentScheduleDetail psd : ps.getDetails()) {
                        accountTemp = accountMap.get(psd.getInvestorId());
                        //待收本金,待收利息
                        accountTemp.setUnReceiveInterest(accountTemp.getUnReceiveInterest().add(psd.getInterest()));
                        if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                            accountTemp.setUnReceivePrincipal(accountTemp.getUnReceivePrincipal().add(psd.getPrincipal()));
                        }
                    }
                }
                //对投资账户进行统一的更新
                for (Account account : accountMap.values()) {
                    accountService.update(account);
                }
                for (ExpAccount account : expAccountMap.values()) {
                    expAccountService.update(account);
                }
            } else {
                //如果审核拒绝
                //同满标一审
                auditReject(bidRequest);
            }
            this.update(bidRequest);
        }
    }

    @Override
    public void applyExp(BidRequest bidRequest) {
        if (bidRequest.getBidRequestAmount().compareTo(BidConst.SMALLEST_BIDREQUEST_AMOUNT) >= 0 &&//借款金额>=系统规定最小借款金额
                bidRequest.getMinBidAmount().compareTo(BidConst.SMALLEST_BID_AMOUNT) >= 0//该次最小投标>=系统设定最小投标
                ) {
            BidRequest br = new BidRequest();
            br.setApplyTime(new Date());//借款时间
            br.setBidRequestAmount(bidRequest.getBidRequestAmount());//借款金额
            br.setBidRequestState(BidConst.BIDREQUEST_STATE_PUBLISH_PENDING);//设置借款状态:待发布
            br.setBidRequestType(BidConst.BIDREQUEST_TYPE_EXP);//设置借款类型:体验标
            br.setCreateUser(UserContext.getCurrent());//借款人
            br.setCurrentRate(bidRequest.getCurrentRate());//借款利率
            br.setDisableDays(bidRequest.getDisableDays());//招标天数
            br.setMinBidAmount(bidRequest.getMinBidAmount());//最小招标金额
            br.setMonthes2Return(bidRequest.getMonthes2Return());//借款的日期
            br.setReturnType(BidConst.RETURN_TYPE_MONTH_INTEREST_PRINCIPAL);//还款方式:按月分期
            br.setTitle(bidRequest.getTitle());//借款标题
            //该次借款的总利息---->应该判断如何计算利息,按月还是按天算
            br.setTotalRewardAmount(CalculatetUtil.calTotalInterest(bidRequest.getReturnType(), bidRequest.getBidRequestAmount(), bidRequest.getCurrentRate(), bidRequest.getMonthes2Return()));
            this.save(br);//保存入库
        }
    }

    private List<PaymentSchedule> creatPaymentScheduleList(BidRequest bidRequest) {
        //创建还款对象,创建个数由还款期数决定
        List<PaymentSchedule> paymentScheduleList = new ArrayList<>();
        PaymentSchedule ps;
        BigDecimal principalTemp = BidConst.ZERO;
        BigDecimal interestTemp = BidConst.ZERO;
        for (int i = 0; i < bidRequest.getMonthes2Return(); i++) {
            ps = new PaymentSchedule();
            //设置相关属性
            ps.setBidRequestId(bidRequest.getId());//关联借款对象
            ps.setBidRequestTitle(bidRequest.getTitle());//借款标题
            ps.setBidRequestType(bidRequest.getBidRequestType());//标的类型
            ps.setBorrowUserId(bidRequest.getCreateUser().getId());//借款人id
            ps.setMonthIndex(i + 1);//第几个月的还款
            ps.setDeadLine(DateUtils.addMonths(bidRequest.getPublishTime(), i + 1));//还款的截止日期
            ps.setReturnType(bidRequest.getReturnType());//还款方式
            //判断是否是最后一期
            //ps.setTotalAmount();
            //ps.setPrincipal();
            //ps.setInterest();
            if (i == bidRequest.getMonthes2Return() - 1) {
                //是最后一期
                //该期本金=总应还本金-(N-1期的本金之和)
                ps.setPrincipal(bidRequest.getBidRequestAmount().subtract(principalTemp));
                //该期利息=总应还利息-(N-1期的利息之和)
                ps.setInterest(bidRequest.getTotalRewardAmount().subtract(interestTemp));
                //该期应还总金额=该期本金+该期利息
                ps.setTotalAmount(ps.getPrincipal().add(ps.getInterest()));
            } else {
                //不是最后一期
                //应还的总金额
                ps.setTotalAmount(CalculatetUtil.calMonthToReturnMoney(bidRequest.getReturnType(), bidRequest.getBidRequestAmount(), bidRequest.getCurrentRate(), i + 1, bidRequest.getMonthes2Return()));
                //当月应还利息
                ps.setInterest(CalculatetUtil.calMonthlyInterest(bidRequest.getReturnType(), bidRequest.getBidRequestAmount(), bidRequest.getCurrentRate(), i + 1, bidRequest.getMonthes2Return()));
                //当月应还本金
                ps.setPrincipal(ps.getTotalAmount().subtract(ps.getInterest()));
                principalTemp = principalTemp.add(ps.getPrincipal());
                interestTemp = interestTemp.add(ps.getInterest());
            }
            paymentScheduleService.save(ps);
            //生成还款明细集合
            creatPaymentScheduleDetailList(bidRequest, ps);
            paymentScheduleList.add(ps);
        }
        return paymentScheduleList;
    }

    private void creatPaymentScheduleDetailList(BidRequest bidRequest, PaymentSchedule ps) {
        //创建几个还款对象,由投资人个数决定
        PaymentScheduleDetail psd;
        Bid bid;
        BigDecimal principalTemp = BidConst.ZERO;
        BigDecimal interestTemp = BidConst.ZERO;
        for (int i = 0, len = bidRequest.getBids().size(); i < len; i++) {
            bid = bidRequest.getBids().get(i);
            psd = new PaymentScheduleDetail();
            //设置相关属性
            psd.setBidAmount(bid.getAvailableAmount());//投标多少钱
            psd.setBidId(bid.getId());//投标对象id
            psd.setBidRequestId(bidRequest.getId());//借款对象id
            psd.setBorrowUser(bidRequest.getCreateUser());//借款人
            psd.setDeadLine(ps.getDeadLine());//还款截止日期
            psd.setInvestorId(bid.getBidUser().getId());//投资人id
            psd.setMonthIndex(ps.getMonthIndex());//哪一期的还款
            psd.setPaymentScheduleId(ps.getId());//还款对象id
            psd.setReturnType(ps.getReturnType());//还款类型

            //判断是否最后一个投资人
            //psd.setTotalAmount();
            //psd.setPrincipal();
            //psd.setInterest();
            if (i == len - 1) {
                //是最后一个投资人
                //该期还款明细的本金=总的-(n-1)期
                psd.setPrincipal(ps.getPrincipal().subtract(principalTemp));
                psd.setInterest(ps.getInterest().subtract(interestTemp));
                psd.setTotalAmount(psd.getPrincipal().add(psd.getInterest()));
            } else {
                //不是最后一个投资人
                //投资的比例
                BigDecimal bidRate = bid.getAvailableAmount().divide(bidRequest.getBidRequestAmount(), BidConst.STORE_SCALE, RoundingMode.HALF_UP);
                //设置还款明细本金
                psd.setPrincipal(bidRate.multiply(ps.getPrincipal()).setScale(BidConst.STORE_SCALE, RoundingMode.HALF_UP));
                //设置还款明细利息
                psd.setInterest(bidRate.multiply(ps.getInterest()).setScale(BidConst.STORE_SCALE, RoundingMode.HALF_UP));
                //设置总金额
                psd.setTotalAmount(psd.getInterest().add(psd.getPrincipal()));
                principalTemp = principalTemp.add(psd.getPrincipal());
                interestTemp = interestTemp.add(psd.getInterest());
            }
            paymentScheduleDetailService.save(psd);
            ps.getDetails().add(psd);
        }
    }

    private void creatBidRequestAuditHistory(BidRequest bidRequest, int state, int auditType, String remark) {
        //创建审核历史对象
        BidRequestAuditHistory brah = new BidRequestAuditHistory();
        brah.setRemark(remark);
        brah.setAuditTime(new Date());
        brah.setAuditor(UserContext.getCurrent());
        brah.setApplier(bidRequest.getCreateUser());
        brah.setApplyTime(new Date());//这里应该是投满标的时候同时设置的值
        brah.setBidRequestId(bidRequest.getId());
        brah.setAuditType(auditType);
        if (state == BidRequestAuditHistory.STATE_PASS) {
            //如果审核通过
            brah.setState(BidRequestAuditHistory.STATE_PASS);
        } else {
            //如果审核拒绝
            brah.setState(BidRequestAuditHistory.STATE_REJECT);
        }
        bidRequestAuditHistoryService.save(brah);
    }

    private void auditReject(BidRequest bidRequest) {
        //审核拒绝
        //修改借款对象状态和投标对象状态---->满审拒绝
        bidRequest.setBidRequestState(BidConst.BIDREQUEST_STATE_REJECTED);
        bidService.updateState(bidRequest.getId(), BidConst.BIDREQUEST_STATE_REJECTED);
        //修改投资人账户信息,解冻金额
        Account bidUserAccount;
        ExpAccount expAccount;
        Map<Long, Account> accountMap = new HashMap<>();
        Map<Long, ExpAccount> expAccountMap = new HashMap<>();
        Long bidUserAccountId;
        for (Bid bid : bidRequest.getBids()) {
            bidUserAccountId = bid.getBidUser().getId();
            if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                bidUserAccount = accountMap.get(bidUserAccountId);
                if (bidUserAccount == null) {
                    bidUserAccount = accountService.get(bid.getBidUser().getId());
                    accountMap.put(bidUserAccountId, bidUserAccount);
                }
                bidUserAccount.setFreezedAmount(bidUserAccount.getFreezedAmount().subtract(bid.getAvailableAmount()));
                bidUserAccount.setUsableAmount(bidUserAccount.getUsableAmount().add(bid.getAvailableAmount()));
                accountFlowService.createBidFailedFlow(bidUserAccount, bid.getAvailableAmount());
            } else {
                expAccount = expAccountMap.get(bidUserAccountId);
                if (expAccount == null) {
                    expAccount = expAccountService.get(bid.getBidUser().getId());
                    expAccountMap.put(bidUserAccountId, expAccount);
                }
                expAccount.setFreezedAmount(expAccount.getFreezedAmount().subtract(bid.getAvailableAmount()));
                expAccount.setUsableAmount(expAccount.getUsableAmount().add(bid.getAvailableAmount()));
                //生成投标失败流水
                expAccountFlowService.creatBidFailedFlow(expAccount, bid.getAvailableAmount());
            }
        }
        //对对象进行统一修改
        for (Account account : accountMap.values()) {
            accountService.update(account);
        }
        for (ExpAccount account : expAccountMap.values()) {
            expAccountService.update(account);
        }
        //修改借款人信息,移除借款中状态码
        if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
            Userinfo creatUserUserinfo = userinfoService.get(bidRequest.getCreateUser().getId());
            creatUserUserinfo.removeState(BitStatesUtils.OP_HAS_BIDREQUEST_PROCESS);
            userinfoService.update(creatUserUserinfo);//更新数据库
        }
    }
}
