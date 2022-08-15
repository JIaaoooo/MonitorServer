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
            "execution(* com.example.monitorserver.controller.ProjectController.getAllProject()) ||"+
            "execution(* com.example.monitorserver.controller.UserController.freezeUser()) ||"+
            "execution(* com.example.monitorserver.controller.UserController.forceLogout())")
    public Result jurisdiction(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("管理员aop检测");
        String token = request.getHeader("Authorization");
        log.debug("aop检测"+token);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        //查看keys
        Iterator<String> iterator = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg().concat("*")).iterator();
        while (iterator.hasNext()){
            log.debug("redis中缓存的key"+iterator.next());
        }
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        int position = user.getPosition();
        log.debug(user.toString());
        if (position==9){
            return (Result) pjp.proceed();
        }
        return new Result(ResultEnum.REQUEST_FALSE);
    }
}
