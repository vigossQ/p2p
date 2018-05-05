package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.business.query.BidRequestQueryObject;
import cn.wolfcode.p2p.business.service.IBidRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Autowired
    private IBidRequestService bidRequestService;

    @RequestMapping("index")
    public String index(BidRequestQueryObject qo, Model model){
        //bidRequests(信用标)
        qo.setStates(new int[]{BidConst.BIDREQUEST_STATE_BIDDING,BidConst.BIDREQUEST_STATE_PAYING_BACK,BidConst.BIDREQUEST_STATE_COMPLETE_PAY_BACK});
        model.addAttribute("bidRequests",bidRequestService.queryIndexData(qo,BidConst.BIDREQUEST_TYPE_NORMAL));
        //体验标
        model.addAttribute("expBidRequests",bidRequestService.queryIndexData(qo,BidConst.BIDREQUEST_TYPE_EXP));
        return "main";
    }
}
