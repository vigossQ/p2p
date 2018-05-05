package cn.wolfcode.p2p.base.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class IpLog extends BaseDomain {
    public static final int LOGIN_SUCCESS = 0;//登陆成功
    public static final int LOGIN_FAILED = 1;//登陆失败
    public static final int USERTYPE_USER = 0;//普通用户
    public static final int USERTYPE_MANAGER = 1;//管理员用户
    private String ip;//ip地址
    private String username;//用户名
    private Date loginTime;//登陆时间
    private int state;//状态
    private int userType;//用户类型

    public String getStateDisplay() {
        return state == LOGIN_SUCCESS ? "登陆成功" : "登陆失败";
    }
    public String getUserTypeDisplay() {
        return userType == USERTYPE_USER ? "普通用户" : "管理员用户";
    }
}
