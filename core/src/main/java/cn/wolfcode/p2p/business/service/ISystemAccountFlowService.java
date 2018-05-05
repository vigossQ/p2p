package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.SystemAccount;
import cn.wolfcode.p2p.business.domain.SystemAccountFlow;

import java.math.BigDecimal;

public interface ISystemAccountFlowService {
    int save(SystemAccountFlow systemAccountFlow);

    /**
     * 收取用户借款手续费流水
     *
     * @param systemAccount
     * @param amount
     */
    void createGainAccountManagementCharge(SystemAccount systemAccount, BigDecimal amount);

    /**
     * 收取利息管理费流水
     * @param systemAccount
     * @param amount
     */
    void createGainInterestManagerChargeFlow(SystemAccount systemAccount, BigDecimal amount);

    /**
     * 体验标支付利息流水
     * @param systemAccount
     * @param amount
     */
    void creatPayInterestFlow(SystemAccount systemAccount, BigDecimal amount);
}
