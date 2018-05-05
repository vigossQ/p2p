package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.query.RealAuthQueryObject;
import cn.wolfcode.p2p.base.service.IRealAuthService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 后台的RealAuth
 * Created by Reimu on 2018/3/29.
 */
@Controller
public class RealAuthController {

    @Autowired
    private IRealAuthService realAuthService;

    @RequestMapping("realAuth")
    public String realAuthPage(@ModelAttribute("qo") RealAuthQueryObject qo, Model model) {
        model.addAttribute("pageResult", realAuthService.queryPage(qo));
        return "realAuth/list";
    }

    @RequestMapping("/realAuth_audit")
    @ResponseBody
    public JsonResult audit(Long id, int state, String remark) {
        JsonResult jsonResult = new JsonResult();
        try {
            realAuthService.audit(id,state,remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
