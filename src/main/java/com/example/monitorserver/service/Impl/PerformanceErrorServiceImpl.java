package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.PerformanceErrorMapper;
import com.example.monitorserver.po.PerformanceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.PerformanceErrorService;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: 性能监控 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */
@Service
public class PerformanceErrorServiceImpl extends ServiceImpl<PerformanceErrorMapper, PerformanceError> implements PerformanceErrorService {

    @Autowired
    private PerformanceErrorMapper performanceErrorMapper;

    @Override
    public Result insert(PerformanceError performanceError) {
        MybatisConfig.setDynamicTableName("t_performanceError");
        performanceError.setDate(LocalDateTime.now());
        performanceErrorMapper.insert(performanceError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }
}
