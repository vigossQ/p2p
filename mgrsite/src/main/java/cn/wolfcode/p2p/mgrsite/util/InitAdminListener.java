package cn.wolfcode.p2p.mgrsite.util;

import cn.wolfcode.p2p.base.service.ILogininfoService;
import cn.wolfcode.p2p.business.service.ISystemAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by Reimu on 2018/3/26.
 */

/**
 * 监听Spring容器创建所有bean都已经加载完毕
 */
@Component
public class InitAdminListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ILogininfoService logininfoService;
    @Autowired
    private ISystemAccountService systemAccountService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logininfoService.initSystemAdmin();
        systemAccountService.initSystemAccount();
    }
}
