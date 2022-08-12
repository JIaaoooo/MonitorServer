package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.mapper.UserProjectMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.utils.MybatisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 用户可执行层
 * @author: Jiao
 * @create: 2022-08-08 09:38  20:36
 * @version: 1.2
 */
@Service
@Slf4j
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProject> implements UserProjectService {

    @Autowired
    private UserProjectMapper userProjectMapper;


    @Override
    public Result add(UserProject userProject) {
        MybatisConfig.setDynamicTableName("t_project_user");
        userProjectMapper.insert(userProject);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result select(Map<String, Object> map) {
        MybatisConfig.setDynamicTableName("t_project_user");
        QueryWrapper<UserProject> wrapper = new QueryWrapper<>();
        Iterator<String> iterator = map.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            wrapper.eq(key,map.get(key));
        }
        List<UserProject> list = userProjectMapper.selectList(wrapper);
        return new Result(list);
    }
}
