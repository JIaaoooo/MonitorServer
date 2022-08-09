package com.example.monitorserver.interceptor;

import cn.hutool.json.JSONUtil;
import com.example.monitorserver.constant.RedisEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor server
 * @description: 接口拦截器
 * @author: Jiao
 * @create: 2022-08-08 14：55
 */
public class LoginInterceptor implements HandlerInterceptor {


    private String token;

    private RedisTemplate<String,Object> redisTemplate;

    /**由于在MvcConfig中，这个类是通过new出来的，所以不能通过@AutoWired的方法注入**/
    public LoginInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        token = request.getHeader("Authorization");
        Map<Object, Object> map = redisTemplate.opsForHash().entries(token);
        if(token!=null && map!=null){
            //已登录,并且刷新token
            redisTemplate.expire(token,RedisEnum.TOKEN_EXITS.getCode(), TimeUnit.HOURS);
            return true;
         }
        //错误给予返回提示信息
        HashMap<String,String> result = new HashMap<>();
        result.put("cause","无权限访问，请登录");
        response.getWriter().write(JSONUtil.toJsonStr(result));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            redisTemplate.delete(RedisEnum.LOGIN_TOKEN.getMsg() + token);
    }
}
