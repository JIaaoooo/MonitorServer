/*
package com.example.monitorserver.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.po.User;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

*/
/**
 * @program: monitor server
 * @description: 监控项目拦截器
 * @author: Jiao
 * @create: 2022-08-08 14：55
 *//*

@Slf4j
public class ManagerInterceptor extends HandlerInterceptorAdapter {

    private String token;

    private RedisTemplate<String,Object> redisTemplate;

    */
/**由于在MvcConfig中，这个类是通过new出来的，所以不能通过@AutoWired的方法注入**//*

    public ManagerInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }
        token = request.getHeader("Authorization");
        log.debug("管理员权限检测");
        //TODO 1.通过token获取user对象
        log.debug(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);

        Iterator<String> iterator = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg().concat("*")).iterator();
        while (iterator.hasNext()){
            log.debug("redis中缓存的key"+iterator.next());
        }
        log.debug(entries.toString());
        redisTemplate.opsForHash().putAll(RedisEnum.LOGIN_TOKEN.getMsg()+token,entries );
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        int position = user.getPosition();
        log.debug(user.toString());
        if (position==9){
            return true;
        }
        HashMap<String,String> result = new HashMap<>();
        result.put("cause","无权限访问");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
        return false;
    }
}
*/
