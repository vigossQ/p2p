package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.query.UserFileAuthQueryObject;
import cn.wolfcode.p2p.base.service.IUserFileService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserFileAuthController {

    @Autowired
    private IUserFileService userFileService;

    @RequestMapping("/userFileAuth")
    public String userFileAuthPage(@ModelAttribute("qo")UserFileAuthQueryObject qo, Model model) {
        model.addAttribute("pageResult",userFileService.queryPage(qo));
        return "userFileAuth/list";
    }

    @RequestMapping("/userFile_audit")
    @ResponseBody
    public JsonResult audit(Long id,int state,int score,String remark){
        JsonResult jsonResult = new JsonResult();
        try {
            userFileService.audit(id,state,score,remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
