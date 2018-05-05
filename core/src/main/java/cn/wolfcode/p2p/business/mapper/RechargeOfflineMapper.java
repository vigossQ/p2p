package cn.wolfcode.p2p.business.mapper;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.RechargeOffline;
import java.util.List;

public interface RechargeOfflineMapper {

    int insert(RechargeOffline record);

    RechargeOffline selectByPrimaryKey(Long id);

    List<RechargeOffline> selectList(QueryObject qo);

    int updateByPrimaryKey(RechargeOffline record);
}