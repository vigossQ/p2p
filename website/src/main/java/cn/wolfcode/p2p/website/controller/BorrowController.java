package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.domain.UserFile;
import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.service.IRealAuthService;
import cn.wolfcode.p2p.base.service.IUserFileService;
import cn.wolfcode.p2p.base.service.IUserinfoService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.domain.BidRequest;
import cn.wolfcode.p2p.business.service.IBidRequestService;
import cn.wolfcode.p2p.business.service.IExpAccountService;
import cn.wolfcode.p2p.website.utils.RequiredLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

/**
 * Created by Reimu on 2018/3/29.
 */
@Controller
public class BorrowController {

    @Autowired
    private IAccountService accountService;
    @Autowired
    private IUserinfoService userinfoService;
    @Autowired
    private IBidRequestService bidRequestService;
    @Autowired
    private IRealAuthService realAuthService;
    @Autowired
    private IUserFileService userFileService;
    @Autowired
    private IExpAccountService expAccountService;

    @RequestMapping("/borrow")
    public String borrowPage(Model model) {
        if (UserContext.getCurrent() == null) {
            return "redirect: /borrow.html";
        } else {
            //account
            model.addAttribute("account", accountService.getCurrent());
            //userinfo
            model.addAttribute("userinfo", userinfoService.getCurrent());
            //creditBorrowScore
            model.addAttribute("creditBorrowScore", BidConst.CREDIT_BORROW_SCORE);
            return "borrow";
        }
    }

    @RequestMapping("/borrowInfo")
    @RequiredLogin
    public String borrowApplyPage(Model model) {
        Userinfo userinfo = userinfoService.getCurrent();
        //判断用户是否已经发起借款申请
        if (userinfo.getHasBidRequestProcess()) {
            //如果已经发起,进入结果页面
            return "borrow_apply_result";
        } else {
            boolean canApplay = bidRequestService.canApplyBorrow(userinfo);
            //如果没有发起,判断用户是否有借款资格,如果没有就进入/borrow
            if (canApplay) {
                //minBidRequestAmount
                model.addAttribute("minBidRequestAmount", BidConst.SMALLEST_BIDREQUEST_AMOUNT);
                //account
                model.addAttribute("account", accountService.getCurrent());
                //minBidAmount
                model.addAttribute("minBidAmount", BidConst.SMALLEST_BID_AMOUNT);
                return "borrow_apply";
            } else {
                return "redirect:/borrow";
            }
        }
    }

    @RequestMapping("borrow_apply")
    public String apply(BidRequest bidRequest) {
        bidRequestService.apply(bidRequest);
        return "redirect:/borrowInfo";
    }

    @RequestMapping("/borrow_info")
    public String borrowInfo(Long id, Model model) {
        //bidRequest
        BidRequest bidRequest = bidRequestService.get(id);
        String returnPage = "";
        if (bidRequest != null) {
            model.addAttribute("bidRequest", bidRequest);
            if (bidRequest.getBidRequestType() == BidConst.BIDREQUEST_TYPE_NORMAL) {
                //userInfo
                Userinfo userinfo = userinfoService.get(bidRequest.getCreateUser().getId());
                model.addAttribute("userInfo", userinfo);
                //realAuth
                model.addAttribute("realAuth", realAuthService.get(userinfo.getRealAuthId()));
                //userFiles
                model.addAttribute("userFiles", userFileService.queryByLogininfoId(bidRequest.getCreateUser().getId(), UserFile.STATE_PASS));
                //self
                model.addAttribute("self", UserContext.getCurrent());
                //account
                if (UserContext.getCurrent() == null) {
                    model.addAttribute("self", false);
                } else {
                    //判断是否是借款人
                    if (UserContext.getCurrent().getId().equals(bidRequest.getCreateUser().getId())) {
                        model.addAttribute("self", true);
                    } else {
                        model.addAttribute("self", false);
                        model.addAttribute("account", accountService.getCurrent());
                    }
                }
                returnPage = "borrow_info";
            } else {
                if (UserContext.getCurrent() != null) {
                    model.addAttribute("expAccount", expAccountService.get(UserContext.getCurrent().getId()));
                }
                returnPage = "exp_borrow_info";
            }
        }
        return returnPage;
    }

    @RequestMapping("/borrow_bid")
    @ResponseBody
    public JsonResult bid(Long bidRequestId, BigDecimal amount) {
        JsonResult jsonResult = new JsonResult();
        try {
            bidRequestService.bid(bidRequestId, amount);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
