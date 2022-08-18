package com.example.monitorserver.controller;


import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.MessageService;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @program: monitor server
 * @description: 申请信息获取层
 * @author: Jiao
 * @create: 2022-08-09 16：07
 */
@RestController
@Slf4j
@RequestMapping(value="/message")
@CrossOrigin("http://localhost:3000")
public class MessageController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    /**
     * 当前用户作为接收方，查询信息库中要发给我的消息（该操作在用户点击进入消息队列后执行）
     * @return 返回其消息  ：返回格式： Application1：xxxx    Application2:xxxx
     */
    @GetMapping("/watch")
    @Secret
    public Result getApplication(){
        //获取当前用户的id
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        String userId = user.getUserId();
        return messageService.getApplication(userId);
    }


}
