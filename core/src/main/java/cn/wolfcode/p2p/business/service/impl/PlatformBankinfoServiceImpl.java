package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.PlatformBankinfo;
import cn.wolfcode.p2p.business.mapper.PlatformBankinfoMapper;
import cn.wolfcode.p2p.business.service.IPlatformBankinfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlatformBankinfoServiceImpl implements IPlatformBankinfoService {

    @Autowired
    private PlatformBankinfoMapper platformBankinfoMapper;

    @Override
    public int save(PlatformBankinfo platformBankinfo) {
        return platformBankinfoMapper.insert(platformBankinfo);
    }

    @Override
    public int update(PlatformBankinfo platformBankinfo) {
        return platformBankinfoMapper.updateByPrimaryKey(platformBankinfo);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<PlatformBankinfo> list = platformBankinfoMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public void saveOrUpdate(PlatformBankinfo platformBankinfo) {
        if (platformBankinfo.getId() == null) {
            this.save(platformBankinfo);
        } else {
            this.update(platformBankinfo);
        }
    }

    @Override
    public List<PlatformBankinfo> selectAll() {
        return platformBankinfoMapper.selectList(new QueryObject());
    }
}
