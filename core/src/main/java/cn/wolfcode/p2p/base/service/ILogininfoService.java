package cn.wolfcode.p2p.base.service;

import cn.wolfcode.p2p.base.domain.Logininfo;

import java.util.List;

public interface ILogininfoService {
    Logininfo register(String username, String password);

    boolean checkUsername(String username);

    Logininfo login(String username, String password, int userType);

    void initSystemAdmin();

    List<Logininfo> queryAutoComplate(String keyword);
}
