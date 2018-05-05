package cn.wolfcode.p2p.base.service;

/**
 * Created by Reimu on 2018/3/27.
 */
public interface IVerifyCodeService {
    /**
     * 发送验证码
     * @param phoneNumber 手机号码
     */
    void sendVerifyCode(String phoneNumber);

    /**
     * 校验验证码
     * @param phoneNumber 手机号码
     * @param verifyCode 验证码
     * @return 是否验证通过
     */
    boolean validate(String phoneNumber, String verifyCode);
}
