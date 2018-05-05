package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.BidRequestAuditHistory;
import cn.wolfcode.p2p.business.mapper.BidRequestAuditHistoryMapper;
import cn.wolfcode.p2p.business.query.BidRequestAuditHistoryQueryObject;
import cn.wolfcode.p2p.business.service.IBidRequestAuditHistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BidRequestAuditHistoryServiceImpl implements IBidRequestAuditHistoryService {

    @Autowired
    private BidRequestAuditHistoryMapper bidRequestAuditHistoryMapper;

    @Override
    public int save(BidRequestAuditHistory bidRequestAuditHistory) {
        return bidRequestAuditHistoryMapper.insert(bidRequestAuditHistory);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<BidRequestAuditHistory> list = bidRequestAuditHistoryMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public List<BidRequestAuditHistory> queryByBidRequestId(Long bidRequestId) {
        BidRequestAuditHistoryQueryObject qo = new BidRequestAuditHistoryQueryObject();
        qo.setBidRequestId(bidRequestId);
        List<BidRequestAuditHistory> audits = bidRequestAuditHistoryMapper.selectList(qo);
        return audits;
    }
}
