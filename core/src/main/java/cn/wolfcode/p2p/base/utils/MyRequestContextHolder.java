package cn.wolfcode.p2p.base.utils;

import javax.servlet.http.HttpServletRequest;

public abstract class MyRequestContextHolder {

    private static ThreadLocal<HttpServletRequest> local = new ThreadLocal<>();

    public static HttpServletRequest getHttpServletRequest() {
        return local.get();
    }

    public static void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        local.set(httpServletRequest);
    }
}
