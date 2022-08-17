package com.example.monitorserver.utils;

import com.example.monitorserver.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
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


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(redisTemplate))
                .excludePathPatterns(
                        "/user/login",
                        "/user/register"
                );

    }
}
