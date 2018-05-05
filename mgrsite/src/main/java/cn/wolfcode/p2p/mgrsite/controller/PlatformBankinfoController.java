package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.business.domain.PlatformBankinfo;
import cn.wolfcode.p2p.business.query.PlatformBankinfoQueryObject;
import cn.wolfcode.p2p.business.service.IPlatformBankinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PlatformBankinfoController {

    @Autowired
    private IPlatformBankinfoService platformBankinfoService;

    @RequestMapping("/companyBank_list")
    public String companyBank_list(@ModelAttribute("qo") PlatformBankinfoQueryObject qo, Model model) {
        model.addAttribute("pageResult", platformBankinfoService.queryPage(qo));
        return "platformbankinfo/list";
    }

    @RequestMapping("/companyBank_update")
    @ResponseBody
    public JsonResult saveOrUpdate(PlatformBankinfo platformBankinfo){
        JsonResult jsonResult = new JsonResult();
        try {
            platformBankinfoService.saveOrUpdate(platformBankinfo);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
