package com.example.monitorserver.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.utils.MapBeanUtil;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor server
 * @description: 用户可执行层
 * @author: Jiao
 * @create: 2022-08-08 09:38  20:36
 * @version: 1.2
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(User user) {
        MybatisConfig.setDynamicTableName("t_user");
        //通过用户名与密码与数据库匹配查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //返回用户ID，权限，用户名，电话，邮箱，解封时间，发布的项目ID，监控的项目ID
        wrapper.eq(User::getUsername,user.getUsername())
                .eq(User::getPassword,user.getPassword());
        User result = userMapper.selectOne(wrapper);
        //返回结果不为空，并且要求用户不被冻结，即为登陆成功
        if(result!=null){
            if(result.getPosition()!=-1){
                //登陆成功，判断redis中是否有index缓存
                if(!redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg())){
                    //缓存中无此数据，获取
                    Result pageProject = projectService.getPageProject(1, 10, 0);
                    List<Project> data = (List<Project>) pageProject.getData();
                    redisTemplate.opsForList().leftPush(RedisEnum.TOKEN_EXITS.getMsg(), data);
                    //设置过期时间
                    redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg(), RedisEnum.TOKEN_EXITS.getCode(), TimeUnit.HOURS);
                }
                return new Result(ResultEnum.REQUEST_SUCCESS, result);
            }
            else{
                //用户冻结
                return new Result(ResultEnum.LOGIN_USER_FROZEN);
            }
        }

        return new Result(ResultEnum.LOGIN_INFORMATION_FALSE);
    }

    @Override
    public Result register(User user) {
        MybatisConfig.setDynamicTableName("t_user");
        //用户名查重
        LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(User::getUsername,user.getUsername());
        if(userMapper.selectOne(wrapper1) !=null){
            return new Result(ResultEnum.REGISTER_NAME_DOUBLE);
        }
        //电话号码查重
        LambdaQueryWrapper<User> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(User::getPhone,user.getPhone());
        if(userMapper.selectOne(wrapper2) !=null){
            return new Result(ResultEnum.REGISTER_PHONE_DOUBLE);
        }


        //无重复后注册
        //获取当前时间作为注册时间
        user.setRegisterDate(LocalDateTime.now());
        userMapper.insert(user);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result update(User user) {
        MybatisConfig.setDynamicTableName("t_user");
        LambdaQueryWrapper<User> wrapper  = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId,user.getUserId());
        userMapper.update(user, wrapper);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }
    @Override
    public Result  getAllUser() {
        MybatisConfig.setDynamicTableName("t_user");
        List<User> users =null;
        // TODO 1.查看已登录，并赋予onLive标签
        Set<String> keys = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg());
        Iterator<String> iterator = keys.iterator();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        while(iterator.hasNext()){
            String key = iterator.next();
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            User user = (User) MapBeanUtil.map2Object(entries, User.class);
            user.setOnLive(1);
            queryWrapper.ne("username",user.getUsername());
            users.add(user);
        }
        // TODO 2.查询余下为登录用户，并存入users集合中
        List<User> selectList = userMapper.selectList(queryWrapper);
        for (int i = 0; i < selectList.size(); i++) {
            users.add(selectList.get(i));
        }
        return new Result(ResultEnum.SELECT_SUCCESS, users);
    }

    @Override
    public Result getByCondition(Map<String,Object>  map){
        MybatisConfig.setDynamicTableName("t_user");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        String key = map.keySet().iterator().next();
        log.debug(key);
        wrapper.like(key,map.get(key));
        List<User> users = userMapper.selectList(wrapper);
        return new Result(ResultEnum.SELECT_LIKE, users);
    }

    @Override
    public Result getByUserID(String userId) {
        MybatisConfig.setDynamicTableName("t_user");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        User user = userMapper.selectOne(wrapper);
        return new Result(user);
    }


    @Override
    public Result freezeUser(String userId,LocalDateTime endTime) {
        MybatisConfig.setDynamicTableName("t_user");
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id",userId)
                .set("position",-1)
                .set("unseal_date",endTime);
        userMapper.update(null,wrapper);
        return new Result(ResultEnum.FREEZE_SUCCESS);
    }


}
