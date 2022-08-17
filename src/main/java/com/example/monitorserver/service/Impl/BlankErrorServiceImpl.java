package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.BlankErrorMapper;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.po.BlankError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: 白屏 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */

@Service
public class BlankErrorServiceImpl extends ServiceImpl<BlankErrorMapper,BlankError> implements BlankErrorService {


    @Autowired
    private BlankErrorMapper blankErrorMapper;

    @Override
    public Result insert(BlankError blankError) {
        blankError.setDate(LocalDateTime.now());
        blankErrorMapper.insert(blankError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Long getBlankCount(String  projectName) {
        QueryWrapper<BlankError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        Long count = blankErrorMapper.selectCount(queryWrapper);
        return count;
    }
}
