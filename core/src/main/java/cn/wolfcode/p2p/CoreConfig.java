package cn.wolfcode.p2p;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-core.properties")
@MapperScan({"cn.wolfcode.p2p.base.mapper","cn.wolfcode.p2p.business.mapper"})
public class CoreConfig {

    /*@Bean
    public ServletListenerRegistrationBean<ServletRequestListener> myServletRequestListener() {
        ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
        bean.setListener(new MyServletRequestListener());
        return bean;
    }*/
}
