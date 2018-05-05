package cn.wolfcode.p2p.business.service.impl;

import cn.wolfcode.p2p.base.domain.Account;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.base.service.IAccountService;
import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.business.domain.RechargeOffline;
import cn.wolfcode.p2p.business.mapper.RechargeOfflineMapper;
import cn.wolfcode.p2p.business.service.IAccountFlowService;
import cn.wolfcode.p2p.business.service.IRechargeOfflineService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RechargeOfflineServiceImpl implements IRechargeOfflineService {

    @Autowired
    private RechargeOfflineMapper rechargeOfflineMapper;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IAccountFlowService accountFlowService;

    @Override
    public int save(RechargeOffline rechargeOffline) {
        return rechargeOfflineMapper.insert(rechargeOffline);
    }

    @Override
    public int update(RechargeOffline rechargeOffline) {
        return rechargeOfflineMapper.updateByPrimaryKey(rechargeOffline);
    }

    @Override
    public RechargeOffline get(Long id) {
        return rechargeOfflineMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo queryPage(QueryObject qo) {
        PageHelper.startPage(qo.getCurrentPage(), qo.getPageSize());
        List<RechargeOffline> list = rechargeOfflineMapper.selectList(qo);
        return new PageInfo(list);
    }

    @Override
    public void apply(RechargeOffline rechargeOffline) {
        RechargeOffline ro = new RechargeOffline();
        ro.setAmount(rechargeOffline.getAmount());
        ro.setBankInfo(rechargeOffline.getBankInfo());
        ro.setNote(rechargeOffline.getNote());
        ro.setTradeCode(rechargeOffline.getTradeCode());
        ro.setTradeTime(rechargeOffline.getTradeTime());
        ro.setApplier(UserContext.getCurrent());//申请人
        ro.setApplyTime(new Date());//申请时间
        ro.setState(RechargeOffline.STATE_NORMAL);
        this.save(ro);
    }

    @Override
    public void audit(Long id, int state, String remark) {
        //判断当前充值记录是否存在,是否处于待审核状态
        RechargeOffline rechargeOffline = this.get(id);
        if (rechargeOffline != null && rechargeOffline.getState() == RechargeOffline.STATE_NORMAL) {
            //设置审核相关属性
            rechargeOffline.setAuditor(UserContext.getCurrent());//审核人
            rechargeOffline.setAuditTime(new Date());//审核时间
            rechargeOffline.setRemark(remark);
            if (state == RechargeOffline.STATE_PASS) {
                //如果审核通过
                rechargeOffline.setState(RechargeOffline.STATE_PASS);
                //增加申请人账户余额
                Account account = accountService.get(rechargeOffline.getApplier().getId());
                account.setUsableAmount(account.getUsableAmount().add(rechargeOffline.getAmount()));
                accountService.update(account);
                //增加充值的流水
                accountFlowService.createRechargeOfflineFlow(account, rechargeOffline.getAmount());
            } else {
                //审核失败
                rechargeOffline.setState(RechargeOffline.STATE_REJECT);
            }
            this.update(rechargeOffline);
        }
    }
}
