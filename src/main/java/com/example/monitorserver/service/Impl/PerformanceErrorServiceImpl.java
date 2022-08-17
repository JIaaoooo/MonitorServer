package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.PerformanceErrorMapper;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.PerformanceErrorService;
import com.example.monitorserver.po.PerformanceError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
        performanceError.setDate(LocalDateTime.now());
        performanceErrorMapper.insert(performanceError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result select(Data data) {
        LocalDateTime dateTime = data.getDate().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        QueryWrapper<PerformanceError> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("date",dateTime);
        // TODO 计算平均值
        queryWrapper.ne(data.getResponseType(),null);
        // TODO 1.获取数量
        Long count = performanceErrorMapper.selectCount(queryWrapper);
        // TODO 2.获取反应时间总和
        String select = "SUM("+data.getResponseType()+") AS consumeTime";
        queryWrapper.select(select);
        PerformanceError performanceError = performanceErrorMapper.selectOne(queryWrapper);
        // TODO 3.计算平均耗时
        Long AvgTime = performanceError.getConsumeTime()/count;
        return new Result(ResultEnum.REQUEST_SUCCESS,AvgTime);
    }
}
