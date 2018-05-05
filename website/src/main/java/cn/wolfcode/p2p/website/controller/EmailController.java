package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.service.IEmailService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {

    @Autowired
    private IEmailService emailService;

    @RequestMapping("/sendEmail")
    @ResponseBody
    public JsonResult sendEmail(String email) {
        JsonResult jsonResult = new JsonResult();
        try {
            emailService.sendEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
