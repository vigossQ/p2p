package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.MailVerify;

/**
 * Created by Reimu on 2018/3/27.
 */
public interface IMailVerifyService {
    int save(MailVerify mailVerify);

    MailVerify selectByKey(String uuid);
}
