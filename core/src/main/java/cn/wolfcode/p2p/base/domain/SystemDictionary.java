package cn.wolfcode.p2p.base.domain;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SystemDictionary extends BaseDomain {
    private String title;//分类名称,如:教育背景
    private String sn;//分类编码 ,如:educationBackground

    public String getJsonString() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("title", title);
        map.put("sn", sn);
        return JSON.toJSONString(map);
    }
}
