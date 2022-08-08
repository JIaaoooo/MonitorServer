package com.example.monitorserver.controller;


import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.emum.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @program: monitor server
 * @description: 用户可执行层
 * @author: Jiao
 * @create: 2022-08-08 09:38
 */
@RestController
@RequestMapping(value = "/user",produces = "application/json;charset=UTF-8")
@Slf4j
public class UserController {
    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private TokenUtil tokenUtil;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user){
        //TODO 1.登录认证
        Result<User> result = userService.login(user);
        if(result.getCode()==200){
            //TODO 2.登陆成功，生成Token,并存入redis缓存
            String token = tokenUtil.createToken(user);
            response.setHeader("Authorization",token);
            log.debug(result.toString());
        }
        return result;
    }



    @PostMapping("/register")
    public Result register(@RequestBody User user){
        System.out.println("1");
        Result result = userService.register(user);
        log.debug(result.toString());
        return result;
    }

    @GetMapping("/logout")
    public Result logout(@RequestBody User user){
        String token = request.getHeader("Authorization");
        //将redis缓存中的token删除
        redisTemplate.delete(token);
        return new Result(ResultEnum.LOGOUT_SUCCESS.getCode(), ResultEnum.LOGOUT_SUCCESS.getMsg(), null);
    }



}
