package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.service.ILogininfoService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogininfoController {

    @Autowired
    private ILogininfoService logininfoService;

    @RequestMapping("/userRegister")
    @ResponseBody
    public JsonResult register(String username, String password) {
        JsonResult jsonResult = new JsonResult();
        try {
            logininfoService.register(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("/checkUsername")
    @ResponseBody
    public boolean checkUsername(String username) {
        return logininfoService.checkUsername(username);
    }
}
