package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.monitorserver.Mapper.UserMapper;
import com.example.monitorserver.emum.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * @program: monitor server
 * @description: 超级管理员实现层
 * @author: Jiao
 * @create: 2022-08-08 09:38  23:16
 * @version: 1.2
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManagerServiceImpl implements ManagerService {


    @Override
    public <T> Result<T> getAllUser(int currentPage, int maxMessage,T bean) {
        Page<bean> page = new Page(currentPage, maxMessage);
        List<> records = page.getRecords();
        return new Result(ResultEnum.SELECT_PAGE.getCode(),ResultEnum.SELECT_PAGE.getMsg(),records);
    }
}
