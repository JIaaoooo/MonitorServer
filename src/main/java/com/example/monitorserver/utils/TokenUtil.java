package com.example.monitorserver.utils;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor server
 * @description: Token工具类
 * @author: Jiao
 * @create: 2022-08-08 10：16
 */
@Component
@Slf4j
public class TokenUtil {




        @Autowired
        private RedisTemplate<String,Object> redisTemplate;



        public String createToken(User user){
            log.debug(user.toString());
            StringBuffer sb=new StringBuffer("");
            //对用户信息进行加密
            try {
                String simpleUUID = IdUtil.simpleUUID();
                sb.append(Md5Utils.getMD5(simpleUUID));
                //将token只存入redis缓存,key的格式为： login:token:xxxxxxxx  value类型为map(存储的是user对象)
                redisTemplate.opsForHash().putAll(RedisEnum.LOGIN_TOKEN.getMsg()+sb, BeanUtil.beanToMap(user));
                redisTemplate.expire(RedisEnum.LOGIN_TOKEN.getMsg()+sb,RedisEnum.TOKEN_EXITS.getCode(),TimeUnit.DAYS);

                //测试代码
                Iterator<String> iterator = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg()+sb).iterator();
                if (!iterator.hasNext()){
                    log.debug("没东西");
                }
                while (iterator.hasNext()){
                    log.debug(iterator.next());
                }

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            return sb.toString();
        }

}
