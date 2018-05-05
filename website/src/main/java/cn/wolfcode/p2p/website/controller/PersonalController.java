package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.service.IExpAccountService;
import cn.wolfcode.p2p.website.utils.RequiredLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Reimu on 2018/3/26.
 */
@Controller
public class PersonalController {

    @Autowired
    private IAccountService accountService;
    @Autowired
    private IUserinfoService userinfoService;
    @Autowired
    private IExpAccountService expAccountService;

    @RequestMapping("/personal")
    @RequiredLogin
    public String personalPage(Model model) {
        model.addAttribute("account", accountService.getCurrent());
        model.addAttribute("userinfo", userinfoService.getCurrent());
        model.addAttribute("expAccount", expAccountService.get(UserContext.getCurrent().getId()));
        return "personal";
    }

    @RequestMapping("/bindPhone")
    @ResponseBody
    public JsonResult bindPhone(String phoneNumber, String verifyCode) {
        JsonResult jsonResult = new JsonResult();
        try {
            userinfoService.bindPhone(phoneNumber, verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("/bindEmail")
    public String bindEmail(String key, Model model) {
        try {
            userinfoService.bindEmail(key);
            model.addAttribute("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return "checkmail_result";
    }
}
