package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import org.springframework.stereotype.Service;

import java.util.Map;

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


    /**
     * 用户更新信息
     * @param user 新的用户信息
     * @return 返回更新结果
     */
    Result update(User user);

    /**
     * 分页获取用户信息
     * @param current 当前页
     * @param max 每页最多展示条数
     * @return user结果
     */
    Result getPageUser(int current,int max);

    /**
     * 通过条件查询（可以模糊查询）
     * @param map   键值对的方式
     * @return  返回查询结果
     */
    Result getByCondition(Map<String,Object> map);
}
