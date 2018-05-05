package cn.wolfcode.p2p.business.query;

import cn.wolfcode.p2p.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequestQueryObject extends QueryObject {
    private int bidRequestState = -1;//借款状态
    private int[] states;
    private int bidRequestType = -1;
}
