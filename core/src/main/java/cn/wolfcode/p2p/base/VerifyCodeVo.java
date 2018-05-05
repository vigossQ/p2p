package cn.wolfcode.p2p.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class VerifyCodeVo implements Serializable {
    private String randomCode;//验证码
    private String phoneNumber;//手机号
    private Date sendTime;//发送时间
}
