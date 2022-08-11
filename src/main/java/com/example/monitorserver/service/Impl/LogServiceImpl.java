package com.example.monitorserver.service.Impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.LogMapper;
import com.example.monitorserver.mapper.StatisticsMapper;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.service.LogService;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Autowired
    private StatisticsMapper statisticsMapper;

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

    @Override
    public Result selectProject(LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT project_id");
        if(startTime!=null && endTime !=null){
            wrapper.between("visit_date",startTime,endTime);
        }
        return new Result(ResultEnum.SELECT_SUCCESS,logMapper.selectList(wrapper));
    }

    @Override
    public void HourAutoSum() {
        //TODO 1.创建表
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH");
        String table = "t_statistics_" + simpleDateFormat.format(date);
        statisticsMapper.createTable(table);
        //TODO 2.获取该时间段，有哪些项目在运作
        String today = sdf.format(date);
        // 日志表名
        String table2 = "visit_"+today;
        //TODO 2.1获取开始时间和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //TODO 2.2转换为LocalDataTime类型
        String endTime = df.format(calendar.getTime());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endTimeL = LocalDateTime.parse(endTime,dtf);
        String startTime = df.format(date);
        LocalDateTime startTimeL = LocalDateTime.parse(startTime,dtf);
        // TODO 3.存储访问量，访问人次信息
        saveVisit(startTimeL,endTimeL,table);
    }

    @Override
    public void DayAutoSum() {
        //TODO 1.创建表
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        // 统计信息表名
        String table = "t_statistics_" + simpleDateFormat.format(date);
        statisticsMapper.createTable(table);
        //TODO 2.获取当天，有哪些项目在运作
        String today = sdf.format(date);
        // 日志表名
        String table2 = "visit_"+today;
        // TODO 3.存储访问量，访问人次信息
        saveVisit(null,null,table);
    }




    private void saveVisit(LocalDateTime startTime,LocalDateTime endTime,String table){
        //TODO 1.获取其该时间段下 的访问量 // 访问人数
        Result result = selectProject(startTime, endTime);
        List<Log> logs = (List<Log>) result.getData();
        Iterator<Log> iterator = logs.iterator();
        //TODO 1.1获取project_id
        while(iterator.hasNext()){
            Log log1 = iterator.next();
            String project_id = log1.getProject_id();
            // TODO 2.1.1通过project_id 作为主要条件，获取该项目的包名
            QueryWrapper<Log> PackageQw = new QueryWrapper<>();
            PackageQw.select("DISTINCT package_name")
                    .eq("project_id",project_id);
            if(startTime!=null&&endTime!=null){
                PackageQw.between("visit_date",startTime,endTime);
            }
            List<Log> packageName_list = logMapper.selectList(PackageQw);
            // TODO 2.1.2以project_id 和 packName去获取访问量，访问人次
            Iterator<Log> package_iter = packageName_list.iterator();
            while(package_iter.hasNext()){
                //包名
                String packageName = package_iter.next().getPackageName();
                QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id",project_id)
                        .eq("package_name",packageName);
                if(startTime!=null&&endTime!=null){
                    queryWrapper.between("visit_date",startTime,endTime);
                }
                //获取得到访问量
                Long packageVisits = logMapper.selectCount(queryWrapper);
                //获取包访问的访问人次
                queryWrapper.select("DISTINCT ip");
                Long packageIP = logMapper.selectCount(queryWrapper);

                // TODO 3.将project_id 、package 写入statistics表中记录
                Statistics statistics = new Statistics();
                statistics.setProject_id(project_id)
                        .setPackageName(packageName)
                        .setPage_view(packageVisits)
                        .setVisits(packageIP);
                MybatisConfig.setDynamicTableName(table);
                statisticsMapper.insert(statistics);
            }

            //TODO 2.2.1 用project_id 获取该项目下的方法名
            QueryWrapper<Log> MethodQW = new QueryWrapper();
            MethodQW.select("DISTINCT package_name")
                    .eq("project_id",project_id);
            if(startTime!=null&&endTime!=null){
                MethodQW.between("visit_date",startTime,endTime);
            }
            List<Log> MethodList = logMapper.selectList(MethodQW);
            Iterator<Log> Method_iter = MethodList.iterator();
            while(Method_iter.hasNext()){
                //获得的到方法名
                String method = Method_iter.next().getMethod();
                //TODO 2.2.2查询该项目  该方法名下的访问量
                QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id",project_id)
                        .eq("method",method);
                if(startTime!=null&&endTime!=null){
                    queryWrapper.between("visit_date",startTime,endTime);
                }
                //获取访问量
                Long MethodVisits = logMapper.selectCount(queryWrapper);
                //获取访问人次
                queryWrapper.select("DISTINCT ip");
                Long MethodIP = logMapper.selectCount(queryWrapper);

                //TODO 3.将数据存入statistic表
                Statistics statistics = new Statistics();
                statistics.setProject_id(project_id)
                        .setMethod(method)
                        .setPage_view(MethodVisits)
                        .setVisits(MethodIP);
                MybatisConfig.setDynamicTableName(table);
                statisticsMapper.insert(statistics);
            }
        }
    }
}

