package cn.wolfcode.p2p.website.iterceptor;

import cn.wolfcode.p2p.base.utils.UserContext;
import cn.wolfcode.p2p.website.utils.RequiredLogin;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Reimu on 2018/3/26.
 */
public class CheckLoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取访问方法
        HandlerMethod method = (HandlerMethod) handler;
        //判断访问资源上是否有注解
        if (method.getMethodAnnotation(RequiredLogin.class) != null) {
            //判断是否登陆
            if (UserContext.getCurrent() == null) {
                response.sendRedirect("/login.html");
                return false;
            }
        }
        return true;
    }
}
