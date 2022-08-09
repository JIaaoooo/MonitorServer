package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.Mapper.UserMapper;
import com.example.monitorserver.emum.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private UserMapper userMapper;

    @Override
    public Result<User> login(User user) {
        //通过用户名与密码与数据库匹配查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //返回用户ID，权限，用户名，电话，邮箱，解封时间，发布的项目ID，监控的项目ID
        wrapper.select(User::getUsername,
                        User::getPhone,
                        User::getEmail,
                        User::getUserId,
                        User::getPosition,
                        User::getPosition,
                        User::getUnsealDate,
                        User::getMessageExist)
                .eq(User::getUsername,user.getUsername());
        User result = userMapper.selectOne(wrapper);
        //返回结果不为空，并且要求用户不被冻结，即为登陆成功
        if(result!=null){
            if(result.getPosition()==0){
                return new Result<>(ResultEnum.LOGIN_SUCCESS.getCode(),ResultEnum.LOGIN_SUCCESS.getMsg(), result);
            }
            else{
                //用户冻结
                return new Result<>(ResultEnum.LOGIN_USER_FROZEN.getCode(),ResultEnum.LOGIN_USER_FROZEN.getMsg(), null);
            }
        }

        return new Result<>(ResultEnum.LOGIN_INFORMATION_FALSE.getCode(), ResultEnum.LOGIN_INFORMATION_FALSE.getMsg(), null);
    }

    @Override
    public Result register(User user) {
        //用户名查重
        LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(User::getUsername,user.getUsername());
        if(userMapper.selectOne(wrapper1) !=null){
            return new Result(ResultEnum.REGISTER_NAME_DOUBLE.getCode(),ResultEnum.REGISTER_NAME_DOUBLE.getMsg(), null);
        }
        //电话号码查重
        LambdaQueryWrapper<User> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(User::getPhone,user.getPhone());
        if(userMapper.selectOne(wrapper2) !=null){
            return new Result(ResultEnum.REGISTER_PHONE_DOUBLE.getCode(),ResultEnum.REGISTER_PHONE_DOUBLE.getMsg(), null);
        }
        //邮箱查重
        LambdaQueryWrapper<User> wrapper3 = new LambdaQueryWrapper<>();
        wrapper3.eq(User::getEmail,user.getEmail());
        if(userMapper.selectOne(wrapper3) !=null){
            return new Result(ResultEnum.REGISTER_EMAIL_DOUBLE.getCode(),ResultEnum.REGISTER_EMAIL_DOUBLE.getMsg(), null);
        }

        //无重复后注册
        userMapper.insert(user);
        return new Result(ResultEnum.REGISTER_SUCCESS.getCode(),ResultEnum.REGISTER_SUCCESS.getMsg(), null);
    }

    @Override
    public Result update(User user) {
        LambdaQueryWrapper<User> wrapper  = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId,user.getUserId());
        userMapper.update(user, wrapper);
        return new Result(ResultEnum.UPDATE_SUCCESS.getCode(), ResultEnum.UPDATE_SUCCESS.getMsg(), null);
    }
    @Override
    public Result  getPageUser(int currentPage, int maxMessage) {
        Page<User> page = new Page(currentPage, maxMessage);
        page = userMapper.selectPage(page,null);
        List<User> records = page.getRecords();
        return new Result(ResultEnum.SELECT_PAGE.getCode(),ResultEnum.SELECT_PAGE.getMsg(),records);
    }

    @Override
    public Result getByCondition(Map<String,Object>  map){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        String key = map.keySet().iterator().next();
        log.debug(key);
        wrapper.like(key,map.get(key));
        List<User> users = userMapper.selectList(wrapper);
        return new Result(ResultEnum.SELECT_LIKE.getCode(), ResultEnum.REGISTER_EMAIL_DOUBLE.getMsg(), users);
    }
}
