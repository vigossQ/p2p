package cn.wolfcode.p2p.business.mapper;

import cn.wolfcode.p2p.business.domain.Bid;
import org.apache.ibatis.annotations.Param;

public interface BidMapper {

    int insert(Bid record);

    Bid selectByPrimaryKey(Long id);

    int updateByPrimaryKey(Bid record);

    void updateState(@Param("bidRequestId") Long bidRequestId, @Param("state") int state);
}