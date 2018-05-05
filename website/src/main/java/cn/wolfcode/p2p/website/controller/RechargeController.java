package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.business.domain.RechargeOffline;
import cn.wolfcode.p2p.business.service.IPlatformBankinfoService;
import cn.wolfcode.p2p.business.service.IRechargeOfflineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RechargeController {

    @Autowired
    private IPlatformBankinfoService platformBankinfoService;
    @Autowired
    private IRechargeOfflineService rechargeOfflineService;

    @RequestMapping("/recharge")
    public String rechargePage(Model model) {
        //banks
        model.addAttribute("banks", platformBankinfoService.selectAll());
        return "recharge";
    }

    @RequestMapping("/recharge_save")
    @ResponseBody
    public JsonResult apply(RechargeOffline rechargeOffline){
        JsonResult jsonResult = new JsonResult();
        try {
            rechargeOfflineService.apply(rechargeOffline);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
