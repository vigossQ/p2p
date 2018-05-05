package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.business.domain.ExpAccount;
import cn.wolfcode.p2p.business.domain.ExpAccountFlow;
import cn.wolfcode.p2p.business.mapper.ExpAccountFlowMapper;
import cn.wolfcode.p2p.business.service.IExpAccountFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional
public class ExpAccountFlowServiceImpl implements IExpAccountFlowService {

    @Autowired
    private ExpAccountFlowMapper expAccountFlowMapper;

    @Override
    public int save(ExpAccountFlow expAccountFlow) {
        return expAccountFlowMapper.insert(expAccountFlow);
    }

    private void creatFlow(ExpAccount expAccount, BigDecimal amount, int actionType, String note) {
        ExpAccountFlow flow = new ExpAccountFlow();
        flow.setActionTime(new Date());
        flow.setActionType(actionType);
        flow.setAmount(amount);
        flow.setExpAccountId(expAccount.getId());
        flow.setFreezedAmount(expAccount.getFreezedAmount());
        flow.setNote(note);
        flow.setUsableAmount(expAccount.getUsableAmount());
        this.save(flow);
    }

    @Override
    public void createBidFlow(ExpAccount expAccount, BigDecimal amount) {
        creatFlow(expAccount, amount, BidConst.EXPMONEY_FLOW_BID, "体验标投标" + amount);
    }

    @Override
    public void creatBidFailedFlow(ExpAccount expAccount, BigDecimal amount) {
        creatFlow(expAccount, amount, BidConst.EXPMONEY_FLOW_BID_FAILED, "体验标投标失败" + amount);
    }

    @Override
    public void creatBidSuccessFlow(ExpAccount expAccount, BigDecimal amount) {
        creatFlow(expAccount, amount, BidConst.EXPMONEY_FLOW_BID_SUCCESS, "体验标投标成功" + amount);
    }

    @Override
    public void creatReceiveFlow(ExpAccount expAccount, BigDecimal amount) {
        creatFlow(expAccount, amount, BidConst.EXPMONEY_FLOW_RETURN_SUCCESS, "体验标退回本金成功" + amount);
    }
}
