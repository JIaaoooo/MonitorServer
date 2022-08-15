package com.example.monitorserver.service.Impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.LogMapper;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.LogService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.StatisticsService;
import com.example.monitorserver.service.apiErrorService;
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
    private static String TodayTable = null;

    static {
        Date date = new Date();
        String today = sdf.format(date);
        TodayTable = "t_visit_"+today;
    }
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private apiErrorService apiErrorService;

    @Override
    public Result createTable() {
        Date date = new Date();
        String today = sdf.format(date);
        TodayTable = "t_visit_"+today;
        MybatisConfig.setDynamicTableName("IF");
        logMapper.createTable(TodayTable);
        return new Result(ResultEnum.CREATE_SUCCESS);
    }

    @Override
    public Result insert(Log log) {
        Date date = new Date();
        String today = sdf.format(date);
        String table = "t_visit_"+today;
        MybatisConfig.setDynamicTableName(table);
        logMapper.insert(log);
        return new Result(ResultEnum.INSERT_SUCCESS);
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
        wrapper.select("DISTINCT project_url");
        if(startTime!=null && endTime !=null){
            wrapper.between("visit_date",startTime,endTime);
        }
        MybatisConfig.setDynamicTableName(TodayTable);
        return new Result(ResultEnum.SELECT_SUCCESS,logMapper.selectList(wrapper));
    }

    public void HourAutoSum() {
        //TODO 1.创建表
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH");
        String table = "t_statistics_" + simpleDateFormat.format(date);
        MybatisConfig.setDynamicTableName("IF");
        statisticsService.createTable(table);
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
        // TODO 3.存储访问量，
        saveVisit(startTimeL,endTimeL,table);
    }



    public void DayAutoSum() {
        //TODO 1.创建表
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        // 统计信息表名
        String table = "t_statistics_" + simpleDateFormat.format(date);
        MybatisConfig.setDynamicTableName("IF");
        statisticsService.createTable(table);
        //TODO 2.获取当天，有哪些项目在运作
        String today = sdf.format(date);
        // 日志表名
        String table2 = "visit_"+today;
        // TODO 3.存储访问量，访问人次信息
        saveVisit(null,null,table);
    }


    public Result getProjectPackage(LocalDateTime startTime,LocalDateTime endTime,String project_url){
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT package_name")
                .eq("project_url",project_url);
        if(startTime!=null&&endTime!=null){
            queryWrapper.between("visit_date",startTime,endTime);
        }
        MybatisConfig.setDynamicTableName(TodayTable);
        List<Log> logs = logMapper.selectList(queryWrapper);
        Iterator<Log> iterator = logs.iterator();
        List<String> packages =  new ArrayList<>();
        while (iterator.hasNext()){
            packages.add(iterator.next().getPackageName());
        }
        return new Result(ResultEnum.SELECT_SUCCESS,packages);
    }


    public Result getProjectMethod(LocalDateTime startTime,LocalDateTime endTime,String project_url){
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT method")
                .eq("project_url",project_url);
        if(startTime!=null&&endTime!=null){
            queryWrapper.between("visit_date",startTime,endTime);
        }
        MybatisConfig.setDynamicTableName(TodayTable);
        List<Log> logs = logMapper.selectList(queryWrapper);
        Iterator<Log> iterator = logs.iterator();
        List<String> methods =  new ArrayList<>();
        while (iterator.hasNext()){
            methods.add(iterator.next().getPackageName());
        }
        return new Result(ResultEnum.SELECT_SUCCESS,methods);
    }


    private void saveVisit(LocalDateTime startTime,LocalDateTime endTime,String table){
        //TODO 1.获取其该时间段下 的访问量 // 访问人数
        Result result = selectProject(startTime, endTime);
        List<Log> logs = (List<Log>) result.getData();
        Iterator<Log> iterator = logs.iterator();
        //TODO 1.1获取project_id
        while(iterator.hasNext()){
            Log log1 = iterator.next();
            String projectUrl = log1.getProjectUrl();
            // 获取url对应的项目名
            String projectName = projectService.getProjectName(projectUrl);
            // TODO 2.1.1通过project_id 作为主要条件，获取该项目的包名
            Result projectPackage = getProjectPackage(startTime, endTime, projectUrl);
            List<String> packages = (List<String>) projectPackage.getData();
            // TODO 2.1.2以project_id 和 packName去获取访问量，访问人次
            for (int i = 0; i < packages.size(); i++) {
                //包名
                String packageName = packages.get(i);
                QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id",projectUrl)
                        .eq("package_name",packageName);
                if(startTime!=null&&endTime!=null){
                    queryWrapper.between("visit_date",startTime,endTime);
                }
                //获取得到访问量
                MybatisConfig.setDynamicTableName(TodayTable);
                Long packageVisits = logMapper.selectCount(queryWrapper);
                //获取包访问的访问人次
                queryWrapper.select("DISTINCT ip");
                MybatisConfig.setDynamicTableName(TodayTable);
                Long packageIP = logMapper.selectCount(queryWrapper);
                //后去包的报错次数

                QueryWrapper<Log> qw = new QueryWrapper<>();
                qw.select("exception");
                if(startTime!=null&&endTime!=null){
                    queryWrapper.between("visit_date",startTime,endTime);
                }
                MybatisConfig.setDynamicTableName(TodayTable);
                Long exception = logMapper.selectCount(qw);
                // TODO 3.将project_id 、package 写入statistics表中记录
                Statistics statistics = new Statistics();
                statistics.setProjectUrl(projectUrl)
                        .setPackageName(packageName)
                        .setViews(packageVisits)
                        .setVisits(packageIP)
                        .setDefeat(exception)
                        .setType("api")
                        .setProjectName(projectName);
                MybatisConfig.setDynamicTableName(table);
                statisticsService.insert(statistics);

                // TODO 4.将得到的api信息汇总到apiError表中
                //TODO 4.1获取汇总表中的数据
                Result select = apiErrorService.select(statistics.getProjectName());
                apiError api = (apiError) select.getData();
                Long visits = api.getVisits();
                Long defeatCount = api.getDefeatCount();
                //更新数据
                api.setVisits(visits+packageVisits);
                api.setDefeatCount(defeatCount+exception);
                //计算报错率
                api.setDefeat(1.0*(visits+packageVisits)/(defeatCount+exception));
                // TODO 4.2更新数据
                apiErrorService.update(api);
            }


            //TODO 2.2.1 用project_id 获取该项目下的方法名
            Result projectMethod = getProjectMethod(startTime, endTime, projectUrl);
            List<String> methods = (List<String>) projectMethod.getData();

            for (int i = 0; i < methods.size(); i++) {
                //获得的到方法名
                String method = methods.get(i);
                //TODO 2.2.2查询该项目  该方法名下的访问量
                QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id",projectUrl)
                        .eq("method",method);
                if(startTime!=null&&endTime!=null){
                    queryWrapper.between("visit_date",startTime,endTime);
                }
                //获取访问量
                MybatisConfig.setDynamicTableName(TodayTable);
                Long MethodVisits = logMapper.selectCount(queryWrapper);
                //获取访问人次
                queryWrapper.select("DISTINCT ip");
                MybatisConfig.setDynamicTableName(TodayTable);
                Long MethodIP = logMapper.selectCount(queryWrapper);

                //TODO 3.将数据存入statistic表
                Statistics statistics = new Statistics();
                statistics.setProjectUrl(projectUrl)
                        .setMethod(method)
                        .setViews(MethodVisits)
                        .setVisits(MethodIP)
                        .setType("api")
                        .setProjectName(projectName);
                MybatisConfig.setDynamicTableName(table);
                statisticsService.insert(statistics);

            }

        }
    }

    /**
     * 定时器判断是否执行自动小时存储
     */
    public  void schedule(){
        Date now = new Date();
        //指定每小时触发  ,判断分钟是否在 58分 - 02分之间
        SimpleDateFormat sdfMin = new SimpleDateFormat("mm");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
        Integer min = Integer.valueOf(sdfMin.format(now));
        if (min>58||min<2){
            log.debug("执行小时记录");
            HourAutoSum();
        }
        //当时刻表到24时 55分后执行
        Integer hour = Integer.valueOf(sdfHour.format(now));
        if(hour == 24 && min > 54){
            log.debug("执行每日记录");
            DayAutoSum();
        }
    }

    @Override
    public Result getCurrentLog(int currentPage,String project_url) {
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_url",project_url);
        Page< Log > page = new Page(currentPage, 1);
        MybatisConfig.setDynamicTableName(TodayTable);
        page = logMapper.selectPage(page, queryWrapper);
        List<Log> logOne = page.getRecords();
        return new Result(ResultEnum.SELECT_SUCCESS,logOne);
    }
}

