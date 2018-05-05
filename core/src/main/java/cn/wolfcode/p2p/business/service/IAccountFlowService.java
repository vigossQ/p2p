package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.base.domain.Account;
import cn.wolfcode.p2p.business.domain.AccountFlow;

import java.math.BigDecimal;

/**
 * Created by Reimu on 2018/4/2.
 */
public interface IAccountFlowService {
    int save(AccountFlow accountFlow);

    /**
     * 线下充值流水
     * @param account
     * @param amount
     */
    void createRechargeOfflineFlow(Account account, BigDecimal amount);

    /**
     * 投标冻结流水
     * @param account
     * @param amount
     */
    void createBidFlow(Account account, BigDecimal amount);

    /**
     * 投标失败流水
     * @param account
     * @param amount
     */
    void createBidFailedFlow(Account account, BigDecimal amount);

    /**
     * 借款成功流水
     * @param account
     * @param amount
     */
    void createBorrowFlow(Account account, BigDecimal amount);

    /**
     * 支付平台付款手续费流水
     * @param account
     * @param amount
     */
    void createPayAccountManagementCharge(Account account, BigDecimal amount);

    /**
     * 投标成功流水
     * @param account
     * @param amount
     */
    void createBidSuccessFlow(Account account, BigDecimal amount);

    /**
     * 还款成功的流水
     * @param account
     * @param amount
     */
    void createPayReturnMoneyFlow(Account account, BigDecimal amount);

    /**
     * 回款成功流水
     * @param account
     * @param amount
     */
    void createGainReturnMoneyFlow(Account account, BigDecimal amount);

    /**
     * 支付平台利息管理费流水
     * @param account
     * @param amount
     */
    void createPayInterestManagerChargeFlow(Account account, BigDecimal amount);

    /**
     * 收取利息流水
     * @param account
     * @param amount
     */
    void createReceiveInterestMoneyFlow(Account account, BigDecimal amount);
}
