package cn.wolfcode.p2p.mgrsite.controller;

import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.business.domain.BidRequest;
import cn.wolfcode.p2p.business.query.BidRequestQueryObject;
import cn.wolfcode.p2p.business.query.PaymentScheduleQueryObject;
import cn.wolfcode.p2p.business.service.IBidRequestService;
import cn.wolfcode.p2p.business.service.IPaymentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Reimu on 2018/4/7.
 */
@Controller
public class ExpBidRequestController {

    @Autowired
    private IBidRequestService bidRequestService;
    @Autowired
    private IPaymentScheduleService paymentScheduleService;

    @RequestMapping("/expBidRequestPublishPage")
    public String expBidRequestPublish(Model model) {
        model.addAttribute("minBidRequestAmount", BidConst.SMALLEST_BIDREQUEST_AMOUNT);
        model.addAttribute("minBidAmount", BidConst.SMALLEST_BID_AMOUNT);
        return "expbidrequest/expbidrequestpublish";
    }

    /**
     * 发布体验标
     *
     * @return
     */
    @RequestMapping("expBidRequestPublish")
    @ResponseBody
    public JsonResult publish(BidRequest bidRequest) {
        JsonResult jsonResult = new JsonResult();
        try {
            bidRequestService.applyExp(bidRequest);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }

    @RequestMapping("expBidRequest_list")
    public String expBidRequestList(@ModelAttribute("qo") BidRequestQueryObject qo, Model model) {
        qo.setBidRequestType(BidConst.BIDREQUEST_TYPE_EXP);
        model.addAttribute("pageResult", bidRequestService.queryPage(qo));
        return "expbidrequest/expbidrequestlist";
    }

    /**
     * 体验标还款列表
     *
     * @return
     */
    @RequestMapping("expBidRequestReturn_list")
    public String expBidRequestReturnList(@ModelAttribute("qo") PaymentScheduleQueryObject qo, Model model) {
        qo.setBidRequestType(BidConst.BIDREQUEST_TYPE_EXP);
        model.addAttribute("pageResult", paymentScheduleService.queryPage(qo));
        return "expbidrequest/expbidrequestreturnlist";
    }

    @RequestMapping("returnMoney")
    @ResponseBody
    public JsonResult returnMoney(Long id) {
        JsonResult jsonResult = new JsonResult();
        try {
            paymentScheduleService.returnMoney(id);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.mark(e.getMessage());
        }
        return jsonResult;
    }
}
