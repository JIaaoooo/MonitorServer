package com.example.monitorserver.aop;

import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class ManagerAop {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Around("execution(* com.example.monitorserver.controller.UserController.getAllUser()) ||"+
            "execution(* com.example.monitorserver.controller.UserController.freezeUser()) ||"+
            "execution(* com.example.monitorserver.controller.UserController.forceLogout())")
    public Result jurisdiction(ProceedingJoinPoint pjp) throws Throwable {
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        int position = user.getPosition();
        if (position==9){
            return (Result) pjp.proceed();
        }
        return new Result(ResultEnum.REQUEST_FALSE);
    }
}
