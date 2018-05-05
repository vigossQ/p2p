package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.domain.Logininfo;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.domain.VedioAuth;
import cn.wolfcode.p2p.base.mapper.VedioAuthMapper;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.service.IVedioAuthService;
import cn.wolfcode.p2p.base.utils.BitStatesUtils;
import cn.wolfcode.p2p.base.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class VedioAuthServiceImpl implements IVedioAuthService {

    @Autowired
    private VedioAuthMapper vedioAuthMapper;
    @Autowired
    private IUserinfoService userinfoService;

    @Override
    public int save(VedioAuth vedioAuth) {
        return vedioAuthMapper.insert(vedioAuth);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<VedioAuth> list = vedioAuthMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public void audit(Long loginInfoValue, int state, String remark) {
        Userinfo userinfo = userinfoService.get(loginInfoValue);
        Logininfo applier;
        if (userinfo != null && !userinfo.getIsVedioAuth()) {
            VedioAuth vedioAuth = new VedioAuth();
            applier = new Logininfo();
            applier.setId(loginInfoValue);
            vedioAuth.setApplier(applier);//申请人
            vedioAuth.setApplyTime(new Date());
            vedioAuth.setAuditor(UserContext.getCurrent());
            vedioAuth.setAuditTime(new Date());
            vedioAuth.setRemark(remark);
            if (state == VedioAuth.STATE_PASS) {
                vedioAuth.setState(VedioAuth.STATE_PASS);
                //给用户添加视频认证状态码
                userinfo.addState(BitStatesUtils.OP_VEDIO_AUTH);
                userinfoService.update(userinfo);
            } else {
                vedioAuth.setState(VedioAuth.STATE_REJECT);
            }
            this.save(vedioAuth);
        }
    }
}
