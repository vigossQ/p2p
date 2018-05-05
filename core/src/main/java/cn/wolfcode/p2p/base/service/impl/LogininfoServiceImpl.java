package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.domain.Account;
import cn.wolfcode.p2p.base.domain.IpLog;
import cn.wolfcode.p2p.base.domain.Logininfo;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.mapper.LogininfoMapper;
import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.service.IIpLogService;
import cn.wolfcode.p2p.base.service.ILogininfoService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.MD5;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.domain.ExpAccount;
import cn.wolfcode.p2p.business.service.IExpAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LogininfoServiceImpl implements ILogininfoService {

    @Autowired
    private LogininfoMapper logininfoMapper;
    @Autowired
    private IUserinfoService userinfoService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IIpLogService iIpLogService;
    @Autowired
    private IExpAccountService expAccountService;

    @Override
    public Logininfo register(String username, String password) {
        //判断数据库中是否已经有此用户
        int count = logininfoMapper.selectByUsername(username);
        if (count > 0) {
            throw new RuntimeException("用户名已存在!");
        }
        //没有用户就保存
        Logininfo logininfo = new Logininfo();
        logininfo.setUsername(username);
        logininfo.setPassword(MD5.encode(password));
        logininfo.setState(Logininfo.STATE_NORMAL);
        logininfo.setUserType(Logininfo.USERTYPE_USER);
        logininfoMapper.insert(logininfo);

        //创建Userinfo用户信息对象和Account账户对象
        Userinfo userinfo = new Userinfo();
        userinfo.setId(logininfo.getId());
        userinfoService.save(userinfo);

        Account account = new Account();
        account.setId(logininfo.getId());
        account.setTradePassword(MD5.encode(password));
        accountService.save(account);

        //创建体验金账户
        ExpAccount expAccount = new ExpAccount();
        expAccount.setId(logininfo.getId());
        expAccountService.save(expAccount);
        //发放体验金
        expAccountService.grantExpMoney(expAccount.getId(),new IExpAccountService.LastTime(1, IExpAccountService.LastTimeUnit.MONTH),
                BidConst.REGISTER_GRANT_EXPMONEY,BidConst.EXPMONEY_TYPE_REGISTER);

        return logininfo;
    }

    @Override
    public boolean checkUsername(String username) {
        //判断数据库中是否已经有此用户
        return logininfoMapper.selectByUsername(username) == 0;
    }

    @Override
    public Logininfo login(String username, String password, int userType) {
        Logininfo logininfo = logininfoMapper.login(username, MD5.encode(password), userType);
        IpLog ipLog = new IpLog();
        ipLog.setLoginTime(new Date());
        ipLog.setIp(UserContext.getIp());
        ipLog.setUsername(username);
        ipLog.setUserType(userType);
        if (logininfo == null) {
            ipLog.setState(IpLog.LOGIN_FAILED);
        } else {
            //存入session中
            ipLog.setState(IpLog.LOGIN_SUCCESS);
            UserContext.setCurrent(logininfo);
        }
        iIpLogService.save(ipLog);
        return logininfo;
    }

    @Override
    public void initSystemAdmin() {
        //判断是否有管理员
        int count = logininfoMapper.selectByUserTpye(Logininfo.USERTYPE_MANAGER);
        if (count == 0) {
            Logininfo admin = new Logininfo();
            admin.setUsername(BidConst.DEFAULT_ADMIN_NAME);
            admin.setPassword(MD5.encode(BidConst.DEFAULT_ADMIN_PASSWORD));
            admin.setUserType(Logininfo.USERTYPE_MANAGER);
            admin.setState(Logininfo.STATE_NORMAL);
            logininfoMapper.insert(admin);
        }
    }

    @Override
    public List<Logininfo> queryAutoComplate(String keyword) {
        return logininfoMapper.queryAutoComplate(keyword);
    }
}
