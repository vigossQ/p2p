package cn.wolfcode.p2p.base.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseAuthDomain extends BaseDomain {
    public static final int STATE_NORMAL = 0;//待审核
    public static final int STATE_PASS = 1;//审核通过
    public static final int STATE_REJECT = 2;//审核拒接

    private Logininfo applier;//申请人
    private int state;//审核状态
    private Logininfo auditor;//审核人
    private Date applyTime;//申请时间
    private Date auditTime;//审核时间
    private String remark;//备注

    public String getStateDisplay() {
        switch (this.state) {
            case STATE_NORMAL:
                return "待审核";
            case STATE_PASS:
                return "审核通过";
            case STATE_REJECT:
                return "审核拒绝";
            default:
                return "";
        }
    }
}
