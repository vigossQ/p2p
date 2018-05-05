package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.Userinfo;

public interface IUserinfoService {
    int save(Userinfo userinfo);

    int update(Userinfo userinfo);

    Userinfo get(long id);

    Userinfo getCurrent();

    /**
     * 手机绑定
     * @param phoneNumber 手机号码
     * @param verifyCode 验证码
     */
    void bindPhone(String phoneNumber, String verifyCode);

    /**
     * 邮箱绑定
     * @param key
     */
    void bindEmail(String key);

    /**
     * 填写基本信息
     * @param userinfo
     */
    void basicSave(Userinfo userinfo);
}
