package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.ExpAccountGrantRecord;

/**
 * Created by Reimu on 2018/4/7.
 */
public interface IExpAccountGrantRecordService {
    int save(ExpAccountGrantRecord expAccountGrantRecord);

    int update(ExpAccountGrantRecord expAccountGrantRecord);

    ExpAccountGrantRecord get(Long id);
}
