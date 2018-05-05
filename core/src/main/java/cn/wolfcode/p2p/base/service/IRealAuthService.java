package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.RealAuth;
import cn.wolfcode.p2p.base.query.QueryObject;
import com.github.pagehelper.PageInfo;

/**
 * Created by Reimu on 2018/3/29.
 */
public interface IRealAuthService {
    int save(RealAuth realAuth);

    int update(RealAuth realAuth);

    RealAuth get(Long id);

    PageInfo queryPage(QueryObject qo);

    /**
     * 实名认证申请
     * @param realAuth
     */
    void realAuthSave(RealAuth realAuth);

    /**
     * 实名认证审核
     * @param id
     * @param state
     * @param remark
     */
    void audit(Long id, int state, String remark);
}
