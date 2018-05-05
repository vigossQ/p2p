package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.service.ISystemDictionaryItemService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Reimu on 2018/3/29.
 */
@Controller
public class UserinfoController {

    @Autowired
    private ISystemDictionaryItemService systemDictionaryItemService;
    @Autowired
    private IUserinfoService userinfoService;

    @RequestMapping("basicInfo")
    public String userinfoPage(Model model){
        //userinfo
        model.addAttribute("userinfo",userinfoService.getCurrent());
        //educationBackgrounds
        model.addAttribute("educationBackgrounds",systemDictionaryItemService.selectByParentSn("educationBackground"));
        //incomeGrades
        model.addAttribute("incomeGrades",systemDictionaryItemService.selectByParentSn("incomeGrade"));
        //marriages
        model.addAttribute("marriages",systemDictionaryItemService.selectByParentSn("marriage"));
        //kidCounts
        model.addAttribute("kidCounts",systemDictionaryItemService.selectByParentSn("kidCount"));
        //houseConditions
        model.addAttribute("houseConditions",systemDictionaryItemService.selectByParentSn("houseCondition"));
        return "userInfo";
    }

    @RequestMapping("basicInfo_save")
    @ResponseBody
    public JsonResult basicSave(Userinfo userinfo){
        JsonResult jsonResult = new JsonResult();
        try {
            userinfoService.basicSave(userinfo);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
