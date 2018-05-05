package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.domain.UserFile;
import cn.wolfcode.p2p.base.service.ISystemDictionaryItemService;
import cn.wolfcode.p2p.base.service.IUserFileService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.website.utils.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class UserFileController {

    @Autowired
    private IUserFileService userFileService;
    @Autowired
    private ISystemDictionaryItemService systemDictionaryItemService;

    @Value("${file.path}")
    private String filePath;

    @RequestMapping("/userFile")
    public String userFilePage(Model model) {
        //逻辑判断
        List<UserFile> unSelectList = userFileService.queryUnSelectFileTypeList();
        //如果集合个数大于0,说明用户存在没有选择风控类型的记录
        if (unSelectList.size() > 0) {
            //userFiles
            model.addAttribute("userFiles", unSelectList);
            //fileTypes
            model.addAttribute("fileTypes", systemDictionaryItemService.selectByParentSn("userFileType"));
            return "userFiles_commit";
        } else {
            //用户在数据表中,要么所有数据都已经选择类型,要么就从来没上传过
            model.addAttribute("userFiles", userFileService.querySelectFileTypeList());
            return "userFiles";
        }
    }

    @RequestMapping("/userFile_selectType")
    @ResponseBody
    public JsonResult selectType(Long[] id, Long[] fileType) {
        JsonResult jsonResult = new JsonResult();
        try {
            userFileService.selectType(id, fileType);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("/userFileUpload")
    @ResponseBody
    public String userFileImage(MultipartFile file) {
        //文件上传
        String imagePath = UploadUtil.upload(file, filePath);
        //把图片保存到数据库中
        userFileService.apply(imagePath);
        //返回路径
        return imagePath;
    }
}
