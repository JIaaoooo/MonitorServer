package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.mapper.StatisticsMapper;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public void insert(Statistics statistics) {
        statisticsMapper.insert(statistics);
    }

    @Override
    public Result getMax(String project_id) {
        //获取
        return null;
    }
}
