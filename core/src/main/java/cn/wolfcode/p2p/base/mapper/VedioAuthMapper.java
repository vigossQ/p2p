package cn.wolfcode.p2p.base.mapper;

import cn.wolfcode.p2p.base.domain.VedioAuth;
import cn.wolfcode.p2p.base.query.QueryObject;

import java.util.List;

public interface VedioAuthMapper {

    int insert(VedioAuth record);

    List<VedioAuth> selectList(QueryObject qo);

}