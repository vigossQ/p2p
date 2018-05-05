package cn.wolfcode.p2p.business.query;

import cn.wolfcode.p2p.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BidRequestAuditHistoryQueryObject extends QueryObject {
    private Long bidRequestId;
}
