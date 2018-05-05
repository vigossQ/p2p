package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.domain.Account;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.business.domain.AccountFlow;
import cn.wolfcode.p2p.business.mapper.AccountFlowMapper;
import cn.wolfcode.p2p.business.service.IAccountFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional
public class AccountFlowServiceImpl implements IAccountFlowService {

    @Autowired
    private AccountFlowMapper accountFlowMapper;

    @Override
    public int save(AccountFlow accountFlow) {
        return accountFlowMapper.insert(accountFlow);
    }

    private void createFlow(Account account, BigDecimal amount, int actionType, String remark){
        AccountFlow flow = new AccountFlow();
        flow.setAccountId(account.getId());
        flow.setActionType(actionType);
        flow.setAmount(amount);
        flow.setTradeTime(new Date());
        flow.setRemark(remark);
        flow.setFreezedAmount(account.getFreezedAmount());
        flow.setUsableAmount(account.getUsableAmount());
        this.save(flow);
    }

    @Override
    public void createRechargeOfflineFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_RECHARGE_OFFLINE,"线下充值" + amount);
    }

    @Override
    public void createBidFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_BID_FREEZED,"投标冻结" + amount);
    }

    @Override
    public void createBidFailedFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_BID_UNFREEZED,"投标失败,取消冻结" + amount);
    }

    @Override
    public void createBorrowFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_BIDREQUEST_SUCCESSFUL,"借款成功:" + amount);
    }

    @Override
    public void createPayAccountManagementCharge(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_CHARGE,"支付平台借款手续费:" + amount);
    }

    @Override
    public void createBidSuccessFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_BID_SUCCESSFUL,"投标成功:" + amount);
    }

    @Override
    public void createPayReturnMoneyFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_RETURN_MONEY,"还款成功:" + amount);
    }

    @Override
    public void createGainReturnMoneyFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_CALLBACK_MONEY,"回款成功:" + amount);
    }

    @Override
    public void createPayInterestManagerChargeFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_INTEREST_SHARE,"支付利息管理费:" + amount);
    }

    @Override
    public void createReceiveInterestMoneyFlow(Account account, BigDecimal amount) {
        createFlow(account,amount,BidConst.ACCOUNT_ACTIONTYPE_CALLBACK_INTEREST_MONEY,"收取利息:" + amount);
    }
}
