package cn.wolfcode.p2p.business.service;

import cn.wolfcode.p2p.base.domain.Userinfo;
import cn.wolfcode.p2p.base.query.QueryObject;
import cn.wolfcode.p2p.business.domain.BidRequest;
import cn.wolfcode.p2p.business.query.BidRequestQueryObject;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;

public interface IBidRequestService {
    int save(BidRequest bidRequest);

    int update(BidRequest bidRequest);

    BidRequest get(Long id);

    PageInfo queryPage(QueryObject qo);

    /**
     * 判断用户是否有借款资格
     *
     * @param userinfo
     * @return
     */
    boolean canApplyBorrow(Userinfo userinfo);

    /**
     * 用户借款申请
     *
     * @param bidRequest
     */
    void apply(BidRequest bidRequest);

    /**
     * 发标前审核
     *
     * @param id
     * @param state
     * @param remark
     */
    void publisAudit(Long id, int state, String remark);

    List<BidRequest> queryIndexData(BidRequestQueryObject qo, int bidRequestType);

    /**
     * 投标逻辑
     *
     * @param bidRequestId
     * @param amount
     */
    void bid(Long bidRequestId, BigDecimal amount);

    /**
     * 满标一审
     *
     * @param id
     * @param state
     * @param remark
     */
    void audit1(Long id, int state, String remark);

    /**
     * 满标二审
     *
     * @param id
     * @param state
     * @param remark
     */
    void audit2(Long id, int state, String remark);

    /**
     * 发布体验标
     *
     * @param bidRequest
     */
    void applyExp(BidRequest bidRequest);
}
