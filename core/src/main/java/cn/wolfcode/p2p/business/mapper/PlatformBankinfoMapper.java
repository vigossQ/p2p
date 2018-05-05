package cn.wolfcode.p2p.business.mapper;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.PlatformBankinfo;

import java.util.List;

public interface PlatformBankinfoMapper {

    int insert(PlatformBankinfo record);

    List<PlatformBankinfo> selectList(QueryObject qo);

    int updateByPrimaryKey(PlatformBankinfo record);
}