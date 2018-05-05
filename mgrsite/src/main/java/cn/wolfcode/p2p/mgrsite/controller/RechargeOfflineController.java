package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.business.query.RechargeOfflineQueryObject;
import cn.wolfcode.p2p.business.service.IPlatformBankinfoService;
import cn.wolfcode.p2p.business.service.IRechargeOfflineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RechargeOfflineController {

    @Autowired
    private IRechargeOfflineService rechargeOfflineService;
    @Autowired
    private IPlatformBankinfoService platformBankinfoService;

    @RequestMapping("/rechargeOffline")
    public String rechargeOfflinePage(@ModelAttribute("qo") RechargeOfflineQueryObject qo, Model model) {
        model.addAttribute("pageResult", rechargeOfflineService.queryPage(qo));
        //banks
        model.addAttribute("banks", platformBankinfoService.selectAll());
        return "rechargeoffline/list";
    }

    @RequestMapping("/rechargeOffline_audit")
    @ResponseBody
    public JsonResult audit(Long id, int state, String remark) {
        JsonResult jsonResult = new JsonResult();
        try {
            rechargeOfflineService.audit(id, state, remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
