package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.SystemDictionary;
import cn.wolfcode.p2p.base.query.QueryObject;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ISystemDictionaryService {
    int save(SystemDictionary systemDictionary);

    int updata(SystemDictionary systemDictionary);

    SystemDictionary get(Long id);

    PageInfo queryPage(QueryObject qo);

    List<SystemDictionary> selectAll();

    void saveOrUpdate(SystemDictionary systemDictionary);
}
