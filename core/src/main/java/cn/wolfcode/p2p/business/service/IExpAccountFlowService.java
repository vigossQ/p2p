package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.ExpAccount;
import cn.wolfcode.p2p.business.domain.ExpAccountFlow;

import java.math.BigDecimal;

public interface IExpAccountFlowService {
    int save(ExpAccountFlow expAccountFlow);

    /**
     * 体验标投标流水
     *
     * @param expAccount
     * @param amount
     */
    void createBidFlow(ExpAccount expAccount, BigDecimal amount);

    /**
     * 体验标投标失败流水
     * @param expAccount
     * @param amount
     */
    void creatBidFailedFlow(ExpAccount expAccount, BigDecimal amount);

    /**
     * 体验标投标成功流水
     * @param expAccount
     * @param amount
     */
    void creatBidSuccessFlow(ExpAccount expAccount, BigDecimal amount);

    /**
     * 体验金结算,收取体验金流水
     * @param expAccount
     * @param amount
     */
    void creatReceiveFlow(ExpAccount expAccount, BigDecimal amount);
}
