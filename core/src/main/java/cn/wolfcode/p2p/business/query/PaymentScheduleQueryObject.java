package cn.wolfcode.p2p.business.query;

import cn.wolfcode.p2p.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PaymentScheduleQueryObject extends QueryObject {
    private Long borrowUserId;
    private Integer bidRequestType;
}
