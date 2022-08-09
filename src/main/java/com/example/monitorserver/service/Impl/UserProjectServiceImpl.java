package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.Mapper.UserProjectMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.UserProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        userProjectMapper.insert(userProject);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }
}
