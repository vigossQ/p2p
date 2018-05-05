package cn.wolfcode.p2p.website.controller;

import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.utils.JsonResult;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.query.PaymentScheduleQueryObject;
import cn.wolfcode.p2p.business.service.IPaymentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Reimu on 2018/4/6.
 */
@Controller
public class ReturnMoneyController {

    @Autowired
    private IAccountService accountService;
    @Autowired
    private IPaymentScheduleService paymentScheduleService;

    @RequestMapping("/borrowBidReturn_list")
    public String returnMoneyPage(@ModelAttribute("qo") PaymentScheduleQueryObject qo, Model model) {
        model.addAttribute("account", accountService.getCurrent());
        qo.setBorrowUserId(UserContext.getCurrent().getId());
        model.addAttribute("pageResult", paymentScheduleService.queryPage(qo));
        return "returnmoney_list";
    }

    @RequestMapping("/returnMoney")
    @ResponseBody
    public JsonResult returnMoney(Long id){
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
