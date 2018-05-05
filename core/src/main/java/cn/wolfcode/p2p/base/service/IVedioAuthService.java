package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.VedioAuth;
import cn.wolfcode.p2p.base.query.QueryObject;
import com.github.pagehelper.PageInfo;

/**
 * Created by Reimu on 2018/3/31.
 */
public interface IVedioAuthService {
    int save(VedioAuth vedioAuth);
    PageInfo queryPage(QueryObject qo);

    /**
     * 视频认证审核
     * @param loginInfoValue
     * @param state
     * @param remark
     */
    void audit(Long loginInfoValue, int state, String remark);
}
