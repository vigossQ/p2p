package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.PaymentSchedule;
import com.github.pagehelper.PageInfo;

public interface IPaymentScheduleService {
    int save(PaymentSchedule paymentSchedule);

    int update(PaymentSchedule paymentSchedule);

    PaymentSchedule get(Long id);

    PageInfo queryPage(QueryObject qo);

    /**
     * 还款逻辑
     * @param id
     */
    void returnMoney(Long id);
}
