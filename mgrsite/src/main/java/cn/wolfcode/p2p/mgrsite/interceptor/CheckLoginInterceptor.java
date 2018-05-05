package cn.wolfcode.p2p.mgrsite.interceptor;

import cn.wolfcode.p2p.base.utils.UserContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Reimu on 2018/3/26.
 */
public class CheckLoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (UserContext.getCurrent()==null){
            response.sendRedirect("/login.html");
            return false;
        }
        return true;
    }
}
