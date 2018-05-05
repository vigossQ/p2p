package cn.wolfcode.p2p.base.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonResult {
    private boolean success = true;
    private String msg;

    public void mark(String msg) {
        this.success = false;
        this.msg = msg;
    }
}
