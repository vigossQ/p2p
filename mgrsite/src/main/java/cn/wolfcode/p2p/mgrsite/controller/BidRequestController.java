package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.domain.UserFile;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.service.IRealAuthService;
import cn.wolfcode.p2p.base.service.IUserFileService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.business.domain.BidRequest;
import cn.wolfcode.p2p.business.query.BidRequestQueryObject;
import cn.wolfcode.p2p.business.service.IBidRequestAuditHistoryService;
import cn.wolfcode.p2p.business.service.IBidRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BidRequestController {

    @Autowired
    private IBidRequestService bidRequestService;
    @Autowired
    private IUserinfoService userinfoService;
    @Autowired
    private IRealAuthService realAuthService;
    @Autowired
    private IUserFileService userFileService;
    @Autowired
    private IBidRequestAuditHistoryService bidRequestAuditHistoryService;


    @RequestMapping("/bidrequest_publishaudit_list")
    public String publishAuditPage(@ModelAttribute("qo") BidRequestQueryObject qo, Model model) {
        //只查询待发布借款对象
        qo.setBidRequestState(BidConst.BIDREQUEST_STATE_PUBLISH_PENDING);
        model.addAttribute("pageResult", bidRequestService.queryPage(qo));
        return "bidrequest/publish_audit";
    }

    @RequestMapping("/bidrequest_publishaudit")
    @ResponseBody
    public JsonResult publishAudit(Long id, int state, String remark) {
        JsonResult jsonResult = new JsonResult();
        try {
            bidRequestService.publisAudit(id, state, remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("/borrow_info")
    public String borrowInfo(Long id, Model model) {
        //bidRequest
        BidRequest bidRequest = bidRequestService.get(id);
        String returnPage = "";
        if (bidRequest != null) {
            model.addAttribute("bidRequest", bidRequest);
            //audits
            model.addAttribute("audits", bidRequestAuditHistoryService.queryByBidRequestId(bidRequest.getId()));
            if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                //userInfo
                Userinfo userinfo = userinfoService.get(bidRequest.getCreateUser().getId());
                model.addAttribute("userInfo", userinfo);
                //realAuth
                model.addAttribute("realAuth", realAuthService.get(userinfo.getRealAuthId()));
                //userFiles
                model.addAttribute("userFiles", userFileService.queryByLogininfoId(bidRequest.getCreateUser().getId(), UserFile.STATE_PASS));
                returnPage = "bidrequest/borrow_info";
            }else if (bidRequest.getBidRequestType()==BidConst.BIDREQUEST_TYPE_EXP){
                returnPage = "expbidrequest/borrow_info";
            }
        }
        return returnPage;
    }

    @RequestMapping("/bidrequest_audit1_list")
    public String audit1Page(@ModelAttribute("qo") BidRequestQueryObject qo, Model model) {
        //只查询满标一审借款对象
        qo.setBidRequestState(BidConst.BIDREQUEST_STATE_APPROVE_PENDING_1);
        model.addAttribute("pageResult", bidRequestService.queryPage(qo));
        return "bidrequest/audit1";
    }

    @RequestMapping("/bidrequest_audit1")
    @ResponseBody
    public JsonResult audit1(Long id, int state, String remark) {
        JsonResult jsonResult = new JsonResult();
        try {
            bidRequestService.audit1(id, state, remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("/bidrequest_audit2_list")
    public String audit2Page(@ModelAttribute("qo") BidRequestQueryObject qo, Model model) {
        //只查询满标二审借款对象
        qo.setBidRequestState(BidConst.BIDREQUEST_STATE_APPROVE_PENDING_2);
        model.addAttribute("pageResult", bidRequestService.queryPage(qo));
        return "bidrequest/audit2";
    }

    @RequestMapping("/bidrequest_audit2")
    @ResponseBody
    public JsonResult audit2(Long id, int state, String remark) {
        JsonResult jsonResult = new JsonResult();
        try {
            bidRequestService.audit2(id, state, remark);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
