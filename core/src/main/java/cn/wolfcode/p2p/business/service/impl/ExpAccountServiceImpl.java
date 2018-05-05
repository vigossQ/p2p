package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.business.domain.ExpAccount;
import cn.wolfcode.p2p.business.domain.ExpAccountFlow;
import cn.wolfcode.p2p.business.domain.ExpAccountGrantRecord;
import cn.wolfcode.p2p.business.mapper.ExpAccountMapper;
import cn.wolfcode.p2p.business.service.IExpAccountFlowService;
import cn.wolfcode.p2p.business.service.IExpAccountGrantRecordService;
import cn.wolfcode.p2p.business.service.IExpAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Reimu on 2018/4/7.
 */
@Service
@Transactional
public class ExpAccountServiceImpl implements IExpAccountService {

    @Autowired
    private ExpAccountMapper expAccountMapper;
    @Autowired
    private IExpAccountGrantRecordService expAccountGrantRecordService;
    @Autowired
    private IExpAccountFlowService expAccountFlowService;

    @Override
    public int save(ExpAccount expAccount) {
        return expAccountMapper.insert(expAccount);
    }

    @Override
    public int update(ExpAccount expAccount) {
        int count = expAccountMapper.updateByPrimaryKey(expAccount);
        if (count == 0) {
            throw new RuntimeException("乐观锁异常");
        }
        return count;
    }

    @Override
    public ExpAccount get(Long id) {
        return expAccountMapper.selectByPrimaryKey(id);
    }

    @Override
    public void grantExpMoney(Long id, LastTime lastTime, BigDecimal expmoney, int expmoneyType) {
        //创建一个发放回收记录对象
        ExpAccountGrantRecord grantRecord = new ExpAccountGrantRecord();
        grantRecord.setAmount(expmoney);
        grantRecord.setGrantDate(new Date());
        grantRecord.setGrantType(expmoneyType);
        grantRecord.setGrantUserId(id);
        grantRecord.setNote("注册发放体验金");
        grantRecord.setReturnDate(lastTime.getReturnDate(new Date()));
        grantRecord.setState(ExpAccountGrantRecord.STATE_NORMAL);
        expAccountGrantRecordService.save(grantRecord);

        //修改体验金账户余额
        ExpAccount expAccount = this.get(id);
        expAccount.setUsableAmount(expAccount.getUsableAmount().add(expmoney));
        this.update(expAccount);

        //增加一条发放体验金流水
        ExpAccountFlow flow = new ExpAccountFlow();
        flow.setActionTime(new Date());
        flow.setActionType(expmoneyType);
        flow.setAmount(expmoney);
        flow.setExpAccountId(id);
        flow.setFreezedAmount(expAccount.getFreezedAmount());
        flow.setNote("注册发放体验金");
        flow.setUsableAmount(expAccount.getUsableAmount());
        expAccountFlowService.save(flow);
    }
}
