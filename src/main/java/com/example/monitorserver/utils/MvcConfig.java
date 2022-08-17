package com.example.monitorserver.utils;

import com.example.monitorserver.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @program: monitor server
 * @description: 拦截器实现层
 * @author: Jiao
 * @create: 2022-08-08 15：42
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/*")
//                //所有地址都可以访问，也可以配置具体地址
//                .allowedOrigins("*")
//                //允许的请求方式
//                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
//                //是否支持跨域Cookie
//                .allowCredentials(true)
//                // 跨域允许时间
//                .maxAge(3600);
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(redisTemplate))
                .excludePathPatterns(
                        "/user/login",
                        "/user/register"
                );
        /*registry.addInterceptor(new ManagerInterceptor(redisTemplate))
                .addPathPatterns(
                        "/user/freezeUser"
                        );*/
    }
}
