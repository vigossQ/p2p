package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.query.IpLogQueryObject;
import cn.wolfcode.p2p.base.service.IIpLogService;
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
    public String ipLog(@ModelAttribute("qo") IpLogQueryObject qo, Model model) {
        PageInfo pageInfo = ipLogService.queryPage(qo);
        model.addAttribute("pageResult", pageInfo);
        return "ipLog/list";
    }
}
