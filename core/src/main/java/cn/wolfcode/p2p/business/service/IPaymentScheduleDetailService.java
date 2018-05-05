package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.PaymentScheduleDetail;

import java.util.Date;

/**
 * Created by Reimu on 2018/4/6.
 */
public interface IPaymentScheduleDetailService {
    int save(PaymentScheduleDetail paymentScheduleDetail);

    int update(PaymentScheduleDetail paymentScheduleDetail);

    PaymentScheduleDetail get(Long id);

    /**
     * 批量修改还款时间
     * @param psId
     * @param payDate
     */
    void updatePayDate(Long psId, Date payDate);
}
