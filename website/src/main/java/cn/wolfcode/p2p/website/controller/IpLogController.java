package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.query.IpLogQueryObject;
import cn.wolfcode.p2p.base.service.IIpLogService;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.website.utils.RequiredLogin;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IpLogController {

    @Autowired
    private IIpLogService ipLogService;

    @RequestMapping("ipLog")
    @RequiredLogin
    public String ipLog(@ModelAttribute("qo") IpLogQueryObject qo, Model model) {
        qo.setUsername(UserContext.getCurrent().getUsername());
        PageInfo pageInfo = ipLogService.queryPage(qo);
        model.addAttribute("pageResult", pageInfo);
        return "iplog_list";
    }
}
