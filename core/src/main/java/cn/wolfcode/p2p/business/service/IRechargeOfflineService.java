package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.RechargeOffline;
import com.github.pagehelper.PageInfo;

/**
 * Created by Reimu on 2018/4/2.
 */
public interface IRechargeOfflineService {
    int save(RechargeOffline rechargeOffline);

    int update(RechargeOffline rechargeOffline);

    RechargeOffline get(Long id);

    PageInfo queryPage(QueryObject qo);

    /**
     * 线下充值
     *
     * @param rechargeOffline
     */
    void apply(RechargeOffline rechargeOffline);

    /**
     * 线下充值审核
     * @param id
     * @param state
     * @param remark
     */
    void audit(Long id, int state, String remark);
}
