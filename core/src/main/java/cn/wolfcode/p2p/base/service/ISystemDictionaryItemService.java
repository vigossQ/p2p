package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.SystemDictionaryItem;
import cn.wolfcode.p2p.base.query.QueryObject;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ISystemDictionaryItemService {
    int save(SystemDictionaryItem systemDictionaryItem);

    int update(SystemDictionaryItem systemDictionaryItem);

    SystemDictionaryItem get(Long id);

    PageInfo queryPage(QueryObject qo);

    void saveOrUpdate(SystemDictionaryItem systemDictionaryItem);

    List<SystemDictionaryItem> selectByParentSn(String sn);
}
