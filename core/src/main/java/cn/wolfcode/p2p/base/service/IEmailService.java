package cn.wolfcode.p2p.base.service;

/**
 * Created by Reimu on 2018/3/27.
 */
public interface IEmailService {
    /**
     * 发送邮件
     * @param email 邮箱地址
     */
    void sendEmail(String email);
}
