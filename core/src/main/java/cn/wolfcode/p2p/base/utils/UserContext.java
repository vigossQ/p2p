package cn.wolfcode.p2p.base.utils;

import cn.wolfcode.p2p.base.VerifyCodeVo;
import cn.wolfcode.p2p.base.domain.Logininfo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class UserContext {

    public static final String USER_IN_SESSION = "logininfo";
    public static final String VERIFYCODE_IN_SESSION = "verifyCode";

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //HttpServletRequest request = MyRequestContextHolder.getHttpServletRequest();
//        System.out.println("request:" + request);
        return request;
    }

    public static void setCurrent(Logininfo logininfo) {
        getRequest().getSession().setAttribute(USER_IN_SESSION, logininfo);
    }

    public static Logininfo getCurrent() {
        return (Logininfo) getRequest().getSession().getAttribute(USER_IN_SESSION);
    }

    public static String getIp() {
        return getRequest().getLocalAddr();
    }

    public static void setVerifyCodeVo(VerifyCodeVo verifyCodeVo) {
        getRequest().getSession().setAttribute(VERIFYCODE_IN_SESSION, verifyCodeVo);
    }

    public static VerifyCodeVo getVerifyCodeVo() {
        return (VerifyCodeVo) getRequest().getSession().getAttribute(VERIFYCODE_IN_SESSION);
    }
}
