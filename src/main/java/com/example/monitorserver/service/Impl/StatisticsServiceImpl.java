package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.StatisticsMapper;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.service.StatisticsService;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public void createTable(String table) {
        statisticsMapper.createTable(table);
    }

    @Override
    public void insert(Statistics statistics) {
        statisticsMapper.insert(statistics);
    }

    @Override
    public Result getMax(String project_id) {
        //获取
        return null;
    }

    @Override
    public Result getHoursData(String project_name) {
        //TODO 1.获得现在的小时数
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HH");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Integer now = Integer.valueOf(sdf.format(calendar.getTime()));
        //TODO 2.从0时开始获取统计数据
        List<Statistics> list = null;
        int count = (~(now-1));
        for (int i = count; i <=0 ; i++) {
            calendar.add(calendar.HOUR_OF_DAY, i);
            String table = sdf2.format(calendar.getTime());
            QueryWrapper<Statistics> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("SUM(views) AS views","SUM(visits) AS visits","SUM(defeat) AS defeat")
                            .eq("project_name",project_name);
            MybatisConfig.setDynamicTableName("t_statistics_"+table);
            Statistics selectOne = statisticsMapper.selectOne(queryWrapper);
            //TODO 3. 计算成功率
            Long defeat = selectOne.getDefeat();
            Long views = selectOne.getViews();
            selectOne.setSuccessRate(1.0*views/defeat);
            list.add(selectOne);

        }

        return new Result(ResultEnum.SELECT_SUCCESS,list);
    }
}
