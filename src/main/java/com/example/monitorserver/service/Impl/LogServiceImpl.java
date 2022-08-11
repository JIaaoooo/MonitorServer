package com.example.monitorserver.service.Impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.LogMapper;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.LogService;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @program: monitor server
 * @description: 监控信息 实现层
 * @author: Jiao
 * @create: 2022-08-10 10:00
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper,Log> implements LogService {

    private final static SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private LogMapper logMapper;

    @Override
    public Result createTable() {
        Date date = new Date();
        String today = sdf.format(date);
        String table = "t_visit_"+today;
        MybatisConfig.setDynamicTableName("IF");
        logMapper.createTable(table);
        return new Result(ResultEnum.CREATE_SUCCESS);
    }

    @Override
    public Result select(HashMap<String,Object> map) {
        QueryWrapper<Log> wrapper = new QueryWrapper();
        //查询的表名
        Date date = new Date();
        String today = sdf.format(date);
        String table = "visit_"+today;
        MybatisConfig.setDynamicTableName(table);
        if (map!=null){
            Iterator<String> keys = map.keySet().iterator();
            while(keys.hasNext()){
                String key = keys.next();
                wrapper.eq(key, map.get(key));
            }
        }
        List<Log> logs = logMapper.selectList(wrapper);
        return new Result(ResultEnum.SELECT_SUCCESS,logs);
    }
}
