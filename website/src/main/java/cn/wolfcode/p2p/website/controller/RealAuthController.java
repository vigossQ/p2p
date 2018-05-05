package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.domain.RealAuth;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.service.IRealAuthService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.website.utils.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Reimu on 2018/3/29.
 */
@Controller
public class RealAuthController {

    @Autowired
    private IUserinfoService userinfoService;
    @Autowired
    private IRealAuthService realAuthService;
    @Value("${file.path}")
    private String filePath;

    @RequestMapping("/realAuth")
    public String realAuthPage(Model model) {
        //逻辑判断
        Userinfo userinfo = userinfoService.getCurrent();
        if (userinfo.getIsRealAuth()) {
            //已经认证
            model.addAttribute("auditing", false);
            model.addAttribute("realAuth", realAuthService.get(userinfo.getRealAuthId()));
            return "realAuth_result";
        } else {
            //没有认证
            if (userinfo.getRealAuthId() == null) {
                //跳转到申请页面
                return "realAuth";
            } else {
                model.addAttribute("auditing", true);
                //跳转到待审核页面
                return "realAuth_result";
            }
        }
    }

    @RequestMapping("realAuth_save")
    @ResponseBody
    public JsonResult realAuthSave(RealAuth realAuth){
        JsonResult jsonResult = new JsonResult();
        try {
            realAuthService.realAuthSave(realAuth);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("/uploadImage")
    @ResponseBody
    public String uploadImage(MultipartFile image){
        return UploadUtil.upload(image,filePath);
    }
}
