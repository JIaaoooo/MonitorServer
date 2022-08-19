package com.example.monitorserver.controller;


import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.utils.TokenUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @program: monitor server
 * @description: 用户可执行层
 * @author: Jiao
 * @create: 2022-08-08 09:38
 */
@RestController
@RequestMapping(value = "/user",produces = "application/json;charset=UTF-8")
@Slf4j
@CrossOrigin("http://localhost:3000")
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

    /**
     * 登录
     * @param user 传递user对象（通过用户名和密码进行校验）
     * @return 返回除了密码之后的信息
     */
    @PostMapping("/login")
    @Secret
    public Result login(@RequestBody User user){
        //TODO 1.登录认证
        System.out.println("user = " + user);
        Result result = userService.login(user);
        if(result.getCode()==200){
            User resultData = (User) result.getData();
            user.setUserId(resultData.getUserId());
            user.setPosition(resultData.getPosition());
            //TODO 2.登陆成功，生成Token,并存入redis缓存
            String token = tokenUtil.createToken(user);
            //response.setHeader("Authorization",token);
            response.addHeader("Authorization",token);
            response.setHeader("Access-Control-Expose-Headers","Authorization");
            log.debug(token);
        }

        return result;
    }


    /**
     * 注册
     * @param user 传递user对象
     * @return
     */
    @PostMapping("/register")
    @Secret
    public Result register(@RequestBody @Validated User user){
        //生成唯一id
        String ID = IdUtil.simpleUUID();
        user.setUserId(ID);
        Result result = userService.register(user);
        log.debug(result.toString());
        return result;
    }

    /**
     * 用户登出
     * @return 返回操作结果
     */
    @GetMapping("/logout")
    @Secret
    public Result logout(){
        String token = request.getHeader("Authorization");
        //将redis缓存中的token删除
        redisTemplate.delete(RedisEnum.LOGIN_TOKEN.getMsg()+token);
        return new Result(ResultEnum.LOGOUT_SUCCESS);
    }

    /**
     * 用户修改个人信息，根据ID去存储新的值
     * @param user 用户信息
     * @return 返回更新结果
     */
    @PutMapping
    @Secret
    public Result update(@RequestBody User user){
        return userService.update(user);
    }

    /**
     * 模糊、条件查询用户   可根据任何值 (在邀请发布者需要调用)
     * @param data  前端数据封装
     * @return 返回该用户（可能为集合）
     */
    @PostMapping("/getUser")
    @Secret//用户邀请其他用户
    public Result getUserByCondition(@RequestBody Data data){
        Map<String,Object> condition = new HashMap<>();
        condition.put("username",data.getUserName());
        return userService.getByCondition(condition);
    }


    /**
     * 管理员冻结用户
     * @param data  接受信息的封装类
     * @return 返回操作执行结果
     */
    @PostMapping("/freezeUser")
    @Secret
    public Result freezeUser(@RequestBody Data data){
        LocalDateTime dateTime = data.getDate().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        return userService.freezeUser(data.getUserId(),dateTime);
    }


    /**
     * 管理员强制用户登出（通过用户名）
     * @param data 前端请求数据封装
     * @return 执行结果
     */
    @PostMapping("/forceLogout")
    @Secret
    public Result forceLogout(@RequestBody Data data){
        String userId = data.getUserId();
        //TODO 1.查询redis中已存在的key对应的user
        Iterator<String> iterator = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg().concat("*")).iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            log.debug(key);
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            User user = (User) MapBeanUtil.map2Object(entries, User.class);
            if (userId.equals(user.getUserId())){
                // TODO 2.删除redis缓存中的用户
                redisTemplate.delete(key);
            }
        }
        //删除失败，未找到该用户
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }
    /**
     * 查看所有用户信息
     * @return 返回用户信息
     */
    @GetMapping("/getAllUser")
    @Secret
    public Result getAllUser(){
        log.debug("获取所有用户");
        return userService.getAllUser();
    }


}
