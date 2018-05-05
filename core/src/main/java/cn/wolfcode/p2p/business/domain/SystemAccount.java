package cn.wolfcode.p2p.business.domain;

import cn.wolfcode.p2p.base.domain.BaseDomain;
import cn.wolfcode.p2p.base.utils.BidConst;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter
public class SystemAccount extends BaseDomain {
    private int version;
    private BigDecimal usableAmount = BidConst.ZERO;//账户可用余额
    private BigDecimal freezedAmount = BidConst.ZERO;//账户冻结金额
}
