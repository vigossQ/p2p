package cn.wolfcode.p2p;

import cn.wolfcode.p2p.mgrsite.interceptor.CheckLoginInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class AppConfig extends WebMvcConfigurerAdapter {

    @Bean
    public CheckLoginInterceptor checkLoginInterceptor(){
        return new CheckLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 处理拦截的请求和放行的请求
         */
        registry.addInterceptor(checkLoginInterceptor())
                .addPathPatterns("/*")
                .excludePathPatterns("/mgrLogin");
    }

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }
}
