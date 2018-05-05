package cn.wolfcode.p2p.base.mapper;

import cn.wolfcode.p2p.base.domain.RealAuth;
import cn.wolfcode.p2p.base.query.QueryObject;

import java.util.List;

public interface RealAuthMapper {

    int insert(RealAuth record);

    RealAuth selectByPrimaryKey(Long id);

    List<RealAuth> selectList(QueryObject qo);

    int updateByPrimaryKey(RealAuth record);
}