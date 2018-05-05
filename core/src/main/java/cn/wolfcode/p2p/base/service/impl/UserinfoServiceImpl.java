package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.domain.MailVerify;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.mapper.UserinfoMapper;
import cn.wolfcode.p2p.base.service.IMailVerifyService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.service.IVerifyCodeService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.BitStatesUtils;
import cn.wolfcode.p2p.base.utils.DateUtil;
import cn.wolfcode.p2p.base.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class UserinfoServiceImpl implements IUserinfoService {

    @Autowired
    private UserinfoMapper userinfoMapper;
    @Autowired
    private IVerifyCodeService verifyCodeService;
    @Autowired
    private IMailVerifyService mailVerifyService;

    @Override
    public int save(Userinfo userinfo) {
        return userinfoMapper.insert(userinfo);
    }

    @Override
    public int update(Userinfo userinfo) {
        int count = userinfoMapper.updateByPrimaryKey(userinfo);
        if (count == 0) {
            throw new RuntimeException("系统繁忙!(乐观锁异常)");
        }
        return count;
    }

    @Override
    public Userinfo get(long id) {
        return userinfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public Userinfo getCurrent() {
        return this.get(UserContext.getCurrent().getId());
    }

    @Override
    public void bindPhone(String phoneNumber, String verifyCode) {
        //1.判断验证码是否正确,判断手机号码是否一致,判断验证码是否过期
        boolean vaild = verifyCodeService.validate(phoneNumber, verifyCode);
        if (!vaild) {
            throw new RuntimeException("验证码验证失败,请重新发送!");
        }
        //2.判断用户是否已经绑定手机号码.
        Userinfo userinfo = this.getCurrent();
        if (userinfo.getHasBindPhone()) {
            throw new RuntimeException("您已经绑定手机号码,请不要重新绑定!");
        }
        userinfo.addState(BitStatesUtils.OP_BIND_PHONE);
        userinfo.setPhoneNumber(phoneNumber);//设置手机号
        //3.给用户添加手机认证的状态码,给userinfo添加一个phoneNumber的值
        this.update(userinfo);
    }

    @Override
    public void bindEmail(String key) {
        //1.查询对应的数据库是否有记录
        MailVerify mailVerify = mailVerifyService.selectByKey(key);
        if (mailVerify == null) {
            throw new RuntimeException("邮箱认证地址有误,请重新发送!");
        }
        //2.判断邮件的时间是否在有效期之内
        if (DateUtil.getBetweenTime(mailVerify.getSendTime(), new Date()) >= BidConst.EMAIL_VAILD_DAY * 24 * 60 * 60) {
            throw new RuntimeException("邮件已过期,请重新发送!");
        }
        //拿到用户,判断用户是否已经绑定邮箱
        Userinfo userinfo = this.get(mailVerify.getUserid());
        if (userinfo.getHasBindEmail()) {
            throw new RuntimeException("您已经绑定邮箱了,请不要重新绑定!");
        }
        //给用户添加状态码,设置email字段值
        userinfo.addState(BitStatesUtils.OP_BIND_EMAIL);
        userinfo.setEmail(mailVerify.getEmail());
        this.update(userinfo);
    }

    @Override
    public void basicSave(Userinfo userinfo) {//页面传递的只有对应的id值
        //1.获取当前用户的userinfo
        Userinfo current = this.getCurrent();
        current.setEducationBackground(userinfo.getEducationBackground());
        current.setHouseCondition(userinfo.getHouseCondition());
        current.setIncomeGrade(userinfo.getIncomeGrade());
        current.setMarriage(userinfo.getMarriage());
        current.setKidCount(userinfo.getKidCount());
        if (!current.getIsBasicInfo()) {//如果没有设置过基本信息就添加基本信息
            current.addState(BitStatesUtils.OP_BASIC_INFO);
        }
        this.update(current);
    }
}
