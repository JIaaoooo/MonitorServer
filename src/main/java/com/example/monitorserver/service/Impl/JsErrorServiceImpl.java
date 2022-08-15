package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.JsErrorMapper;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.service.JsErrorService;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: js异常 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */
@Service
public class JsErrorServiceImpl extends ServiceImpl<JsErrorMapper, JsError> implements JsErrorService {

    @Autowired
    private JsErrorMapper jsErrorMapper;

    @Override
    public Result insert(JsError jsError) {
        MybatisConfig.setDynamicTableName("t_jsError");
        jsError.setDate(LocalDateTime.now());
        jsErrorMapper.insert(jsError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result getUrlError(Data data) {
        MybatisConfig.setDynamicTableName("t_jsError");
        QueryWrapper<JsError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",data.getProjectName())
                .eq("url",data.getUrl());
        Long count = jsErrorMapper.selectCount(queryWrapper);
        JsError jsError = jsErrorMapper.selectOne(queryWrapper);
        jsError.setCount(count);
        return new Result(ResultEnum.REQUEST_SUCCESS,jsError);
    }
}
