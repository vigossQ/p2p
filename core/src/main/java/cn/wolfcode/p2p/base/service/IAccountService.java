package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.Account;

public interface IAccountService {
    int save(Account account);

    int update(Account account);

    Account get(long id);

    Account getCurrent();
}
