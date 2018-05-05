package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.domain.Logininfo;
import cn.wolfcode.p2p.base.query.VedioAuthQueryObject;
import cn.wolfcode.p2p.base.service.ILogininfoService;
import cn.wolfcode.p2p.base.service.IVedioAuthService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class VedioAuthController {

    @Autowired
    private IVedioAuthService vedioAuthService;
    @Autowired
    private ILogininfoService logininfoService;

    @RequestMapping("/vedioAuth")
    public String vedioAuthPage(@ModelAttribute("qo") VedioAuthQueryObject qo, Model model) {
        model.addAttribute("pageResult", vedioAuthService.queryPage(qo));
        return "vedioAuth/list";
    }

    @RequestMapping("/vedioAuth_audit")
    @ResponseBody
    public JsonResult audit(Long loginInfoValue, int state, String remark) {
        JsonResult jsonResult = new JsonResult();
        try {
            vedioAuthService.audit(loginInfoValue, state, remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("vedioAuth_autocomplate")
    @ResponseBody
    public List<Logininfo> autoComplate(String keyword){
        return logininfoService.queryAutoComplate(keyword);
    }
}
