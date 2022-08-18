package com.example.monitorserver.interceptor;

import cn.hutool.json.JSONUtil;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor server
 * @description: 接口拦截器
 * @author: Jiao
 * @create: 2022-08-08 14：55
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {


    private String token;

    private RedisTemplate<String,Object> redisTemplate;

    /**由于在MvcConfig中，这个类是通过new出来的，所以不能通过@AutoWired的方法注入**/
    public LoginInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }
        log.debug(method);
        token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);

        if(token!=null){
                //已登录,并且刷新token
                redisTemplate.expire(RedisEnum.TOKEN_EXITS.getMsg()+token,RedisEnum.TOKEN_EXITS.getCode(), TimeUnit.HOURS);


                try{
                    User user = (User) MapBeanUtil.map2Object(entries, User.class);
                    String userId = user.getUserId();
                }catch (Exception e){
                    request.getSession();
                    Result result = new Result(ResultEnum.USER_EXPIRE);
                    response.getWriter().write(JSONUtil.toJsonStr(result));
                    return false;
                }
                return true;
        }

        return false;
    }

}
