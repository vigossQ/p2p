package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.BidRequestAuditHistory;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IBidRequestAuditHistoryService {
    int save(BidRequestAuditHistory bidRequestAuditHistory);

    PageInfo queryPage(QueryObject qo);

    List<BidRequestAuditHistory> queryByBidRequestId(Long bidRequestId);
}
