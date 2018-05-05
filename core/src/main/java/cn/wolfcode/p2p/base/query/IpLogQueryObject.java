package cn.wolfcode.p2p.base.query;

import cn.wolfcode.p2p.base.utils.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class IpLogQueryObject extends QueryObject {
    private String username;
    private Date beginDate;
    private Date endDate;
    private int state = -1;
    private int userType = -1;

    public String getUsername() {
        return empty2null(username);
    }

    public Date getBeginDate() {
        return beginDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return DateUtil.getEndDate(endDate);
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setEndDate(Date endDate) {
        this.endDate = endDate;

    }
}
