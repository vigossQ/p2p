package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.PlatformBankinfo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IPlatformBankinfoService {
    int save(PlatformBankinfo platformBankinfo);

    int update(PlatformBankinfo platformBankinfo);

    PageInfo queryPage(QueryObject qo);

    void saveOrUpdate(PlatformBankinfo platformBankinfo);

    List<PlatformBankinfo> selectAll();
}
