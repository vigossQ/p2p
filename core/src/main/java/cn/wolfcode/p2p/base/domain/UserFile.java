package cn.wolfcode.p2p.base.domain;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UserFile extends BaseAuthDomain {
    private String image;//图片
    private SystemDictionaryItem fileType;//风控材料类型
    private int score;//分数

    public String getJsonString(){
        Map<String,Object> map = new HashMap<>();
        map.put("id",getId());
        map.put("applier",getApplier().getUsername());
        map.put("fileType",fileType.getTitle());
        map.put("image",image);
        return JSON.toJSONString(map);
    }
}
