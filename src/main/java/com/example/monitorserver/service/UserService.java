package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: 用户服务层
 * @author: Jiao
 * @create: 2022-08-08 11:59
 */

@Service
public interface UserService extends IService<User> {

    /**
     * 登录认证
     * @param user user对象
     * @return 除了密码之外的User信息
     */
    Result<User> login(User user);

    /**
     * 注册
     * @param user user对象
     * @return 成功与否
     */
    Result register(User user);


}
