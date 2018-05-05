package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.business.domain.Bid;

/**
 * Created by Reimu on 2018/4/1.
 */
public interface IBidService {
    int save(Bid bid);
    int update(Bid bid);
    Bid get(Long id);

    /**
     * 批量修改投标对象的状态
     * @param bidRequestId
     * @param state
     */
    void updateState(Long bidRequestId, int state);
}
