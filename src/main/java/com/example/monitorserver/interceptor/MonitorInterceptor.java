package com.example.monitorserver.interceptor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: monitor server
 * @description: 监控项目拦截器
 * @author: Jiao
 * @create: 2022-08-08 14：55
 */
public class MonitorInterceptor implements HandlerInterceptor {

    private String token;

    private RedisTemplate<String,Object> redisTemplate;

    /**由于在MvcConfig中，这个类是通过new出来的，所以不能通过@AutoWired的方法注入**/
    public MonitorInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
