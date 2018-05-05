package cn.wolfcode.p2p.business.mapper;

import cn.wolfcode.p2p.business.domain.ExpAccountGrantRecord;

public interface ExpAccountGrantRecordMapper {

    int insert(ExpAccountGrantRecord record);

    ExpAccountGrantRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ExpAccountGrantRecord record);
}