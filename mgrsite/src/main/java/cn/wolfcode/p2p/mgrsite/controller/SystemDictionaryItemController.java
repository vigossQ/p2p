package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.domain.SystemDictionaryItem;
import cn.wolfcode.p2p.base.query.SystemDictionaryItemQueryObject;
import cn.wolfcode.p2p.base.service.ISystemDictionaryItemService;
import cn.wolfcode.p2p.base.service.ISystemDictionaryService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SystemDictionaryItemController {

    @Autowired
    private ISystemDictionaryItemService systemDictionaryItemService;
    @Autowired
    private ISystemDictionaryService systemDictionaryService;

    @RequestMapping("systemDictionaryItem_list")
    public String systemDictionaryItemPage(@ModelAttribute("qo") SystemDictionaryItemQueryObject qo, Model model) {
        model.addAttribute("pageResult", systemDictionaryItemService.queryPage(qo));
        model.addAttribute("systemDictionaryGroups", systemDictionaryService.selectAll());
        return "systemdic/systemDictionaryItem_list";
    }

    @RequestMapping("systemDictionaryItem_update")
    @ResponseBody
    public JsonResult saveOrUpdate(SystemDictionaryItem systemDictionaryItem){
        JsonResult jsonResult = new JsonResult();
        try {
            if (systemDictionaryItem.getParentId()==null){
                jsonResult.mark("非法操作,请选择数据字典分类");
                return jsonResult;
            }
            systemDictionaryItemService.saveOrUpdate(systemDictionaryItem);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
