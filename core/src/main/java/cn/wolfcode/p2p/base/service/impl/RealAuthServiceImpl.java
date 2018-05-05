package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.domain.RealAuth;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.mapper.RealAuthMapper;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.base.service.IRealAuthService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.BitStatesUtils;
import cn.wolfcode.p2p.base.utils.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Controller
@Transactional
public class RealAuthServiceImpl implements IRealAuthService {

    @Autowired
    private RealAuthMapper realAuthMapper;
    @Autowired
    private IUserinfoService userinfoService;

    @Override
    public int save(RealAuth realAuth) {
        return realAuthMapper.insert(realAuth);
    }

    @Override
    public int update(RealAuth realAuth) {
        return realAuthMapper.updateByPrimaryKey(realAuth);
    }

    @Override
    public RealAuth get(Long id) {
        return realAuthMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<RealAuth> list = realAuthMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public void realAuthSave(RealAuth realAuth) {
        //判断用户是否已经实名认证
        Userinfo current = userinfoService.getCurrent();
        if (!current.getIsRealAuth()) {
            RealAuth ra = new RealAuth();
            ra.setAddress(realAuth.getAddress());//身份证地址
            ra.setApplier(UserContext.getCurrent());//申请人
            ra.setApplyTime(new Date());//申请时间
            ra.setBornDate(realAuth.getBornDate());//出生年月
            ra.setIdNumber(realAuth.getIdNumber());//身份证号
            ra.setImage1(realAuth.getImage1());//身份证正面
            ra.setImage2(realAuth.getImage2());//身份证反面
            ra.setRealName(realAuth.getRealName());//真实姓名
            ra.setSex(realAuth.getSex());//性别
            ra.setState(RealAuth.STATE_NORMAL);//审核状态
            this.save(ra);
            current.setRealAuthId(ra.getId());//实名认证ID
            userinfoService.update(current);
        }

    }

    @Override
    public void audit(Long id, int state, String remark) {
        //1.根据id获取对象,处于待审核状态
        RealAuth realAuth = this.get(id);
        if (realAuth != null && realAuth.getState() == RealAuth.STATE_NORMAL) {
            //设置审核人相关属性
            realAuth.setAuditor(UserContext.getCurrent());//设置审核人
            realAuth.setAuditTime(new Date());//设置审核时间
            realAuth.setRemark(remark);
            Userinfo applierUserinfo = userinfoService.get(realAuth.getApplier().getId());
            if (state == RealAuth.STATE_PASS) {
                realAuth.setState(RealAuth.STATE_PASS);
                //添加实名认证状态码
                applierUserinfo.addState(BitStatesUtils.OP_REAL_AUTH);
                //给userinfo对应的realName,idNumber冗余字段设置值
                applierUserinfo.setRealName(realAuth.getRealName());
                applierUserinfo.setIdNumber(realAuth.getIdNumber());
            } else {
                realAuth.setState(RealAuth.STATE_REJECT);
                //取出userinfo中的realAuthId设置为null
                applierUserinfo.setRealAuthId(null);
            }
            userinfoService.update(applierUserinfo);
            this.update(realAuth);
        }
    }
}
