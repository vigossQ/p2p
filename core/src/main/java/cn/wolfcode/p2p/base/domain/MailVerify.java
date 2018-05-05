package cn.wolfcode.p2p.base.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class MailVerify extends BaseDomain {
    private Long userid;//发送邮件的用户
    private String email;//需要绑定的邮箱
    private Date sendTime;//发送时间
    private String uuid;//邮件中地址拼接的key
}
