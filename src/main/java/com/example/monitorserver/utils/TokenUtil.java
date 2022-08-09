package com.example.monitorserver.utils;


import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor server
 * @description: Token工具类
 * @author: Jiao
 * @create: 2022-08-08 10：16
 */
@Component
public class TokenUtil {




        @Autowired
        private RedisTemplate<String,Object> redisTemplate;



        public String createToken(User user){
            StringBuffer sb=new StringBuffer("token:");
            //对用户信息进行加密
            try {
                sb.append(Md5Utils.getMD5(user.getPassword()));
                sb.append(Md5Utils.getMD5(user.getEmail()));
                sb.append(Md5Utils.getMD5(user.getPhone()));
                //将token只存入redis缓存,key的格式为： login:token:xxxxxxxx  value类型为map(存储的是user对象)
                redisTemplate.opsForHash().putAll(RedisEnum.LOGIN_TOKEN.getMsg()+sb, BeanUtil.beanToMap(user));
                redisTemplate.expire(RedisEnum.LOGIN_TOKEN.getMsg()+sb,RedisEnum.TOKEN_EXITS.getCode(),TimeUnit.HOURS);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            return sb.toString();
        }

}
