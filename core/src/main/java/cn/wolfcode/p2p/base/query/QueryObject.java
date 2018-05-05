package cn.wolfcode.p2p.base.query;

import com.alibaba.druid.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryObject {
    private Integer currentPage = 1;
    private Integer pageSize = 10;

    protected String empty2null(String s) {
        return StringUtils.isEmpty(s) ? null : s;
    }
}
