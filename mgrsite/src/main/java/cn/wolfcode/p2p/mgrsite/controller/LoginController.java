package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.domain.Logininfo;
import cn.wolfcode.p2p.base.service.ILogininfoService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Reimu on 2018/3/26.
 */
@Controller
public class LoginController {

    @Autowired
    private ILogininfoService logininfoService;

    @RequestMapping("mgrLogin")
    @ResponseBody
    public JsonResult mgrLogin(String username, String password) {
        JsonResult jsonResult = new JsonResult();
        Logininfo logininfo = logininfoService.login(username, password, Logininfo.USERTYPE_MANAGER);
        if (logininfo == null) {
            jsonResult.mark("用户名或密码错误!");
        }
        return jsonResult;
    }

    @RequestMapping("index")
    public String index() {
        return "main";
    }
}
