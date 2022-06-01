package com.xxxx.crm.comfig;

import com.xxxx.crm.interceptors.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor creatLoginInterceptor(){
        return new LoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //需要一个实现HandlerInterceptor接口的拦截器实例，这里使用的是LoginInterceptor
        registry.addInterceptor(creatLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**");
    }
}
