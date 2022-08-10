package com.example.monitorserver.controller;


import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.constant.ResultEnum;
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
    public Result login(@RequestBody User user){
        //TODO 1.登录认证
        Result result = userService.login(user);
        if(result.getCode()==200){
            //TODO 2.登陆成功，生成Token,并存入redis缓存
            String token = tokenUtil.createToken(user);
            response.setHeader("Authorization",token);
            log.debug(result.toString());
        }
        return result;
    }


    /**
     * 注册
     * @param user 传递user对象
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        //生成唯一id
        String ID = IdUtil.simpleUUID();
        user.setUserId(ID);
        Result result = userService.register(user);
        log.debug(result.toString());
        return result;
    }

    /**
     * 用户登出
     * @param user 根绝header头中的token值，删除token缓存即为退出
     * @return 返回操作结果
     */
    @GetMapping("/logout")
    public Result logout(@RequestBody User user){
        String token = request.getHeader("Authorization");
        //将redis缓存中的token删除
        redisTemplate.delete(token);
        return new Result(ResultEnum.LOGOUT_SUCCESS.getCode(), ResultEnum.LOGOUT_SUCCESS.getMsg(), null);
    }

    /**
     * 用户修改个人信息，根据ID去存储新的值
     * @param user 用户信息
     * @return 返回更新结果
     */
    @PutMapping
    public Result update(@RequestBody User user){
        return userService.update(user);
    }

    /**
     * 模糊、条件查询用户   可根据任何值 (在邀请发布者需要调用)
     * @param map 传入键值对（json）
     * @return 返回该用户（可能为集合）
     */
    @GetMapping("/getUserByCondition")  //用户邀请其他用户
    public Result getUserByCondition(Map<String,Object> map){
        return userService.getByCondition(map);
    }


    /**
     * 分页查看用户
     * @param currentPage 当前页
     * @param max 每页最多显示的数量
     * @return 返回该页下的用户
     */
    @GetMapping() //超级管理员权限   position为9
    public Result getPageUser(int currentPage,int max){
        return userService.getPageUser(currentPage,max);
    }

    /**
     * 管理员冻结用户
     * @param userId  用户的Id
     * @return 返回操作执行结果
     */
    public Result freezeUser(String userId){
        return userService.freezeUser(userId);
    }
}
