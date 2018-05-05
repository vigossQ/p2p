package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.PaymentScheduleDetail;
import cn.wolfcode.p2p.business.domain.SystemAccount;

/**
 * Created by Reimu on 2018/4/6.
 */
public interface ISystemAccountService {
    int save(SystemAccount systemAccount);

    int update(SystemAccount systemAccount);

    SystemAccount getCurrent();

    void initSystemAccount();

    /**
     * 体验标结算支付利息
     * @param psd
     */
    void payInterest(PaymentScheduleDetail psd);
}
