package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.business.domain.PaymentScheduleDetail;
import cn.wolfcode.p2p.business.domain.SystemAccount;
import cn.wolfcode.p2p.business.mapper.SystemAccountMapper;
import cn.wolfcode.p2p.business.service.ISystemAccountFlowService;
import cn.wolfcode.p2p.business.service.ISystemAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Reimu on 2018/4/6.
 */
@Service@Transactional
public class SystemAccountServiceImpl implements ISystemAccountService {

    @Autowired
    private SystemAccountMapper systemAccountMapper;
    @Autowired
    private ISystemAccountFlowService systemAccountFlowService;

    @Override
    public int save(SystemAccount systemAccount) {
        return systemAccountMapper.insert(systemAccount);
    }

    @Override
    public int update(SystemAccount systemAccount) {
        int count = systemAccountMapper.updateByPrimaryKey(systemAccount);
        if (count==0){
            throw new RuntimeException("乐观锁异常");
        }
        return count;
    }

    @Override
    public SystemAccount getCurrent() {
        return systemAccountMapper.selectCurrent();
    }

    @Override
    public void initSystemAccount() {
        if (this.getCurrent()==null){
            SystemAccount account = new SystemAccount();
            this.save(account);
        }
    }

    @Override
    public void payInterest(PaymentScheduleDetail psd) {
        SystemAccount account = getCurrent();
        //减少可用余额
        account.setUsableAmount(account.getUsableAmount().subtract(psd.getInterest()));
        //生成流水
        systemAccountFlowService.creatPayInterestFlow(account,psd.getInterest());
    }
}
