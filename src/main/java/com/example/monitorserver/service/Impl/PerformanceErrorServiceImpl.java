package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.PerformanceErrorMapper;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.PerformanceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        queryWrapper.lt("date", dateTime);
        // TODO 计算平均值
        queryWrapper.ne(data.getResponseType(), null);
        // TODO 1.获取数量
        Long count = performanceErrorMapper.selectCount(queryWrapper);
        // TODO 2.获取反应时间总和
        String select = "SUM(" + data.getResponseType() + ") AS consumeTime";
        queryWrapper.select(select);
        PerformanceError performanceError = performanceErrorMapper.selectOne(queryWrapper);
        // TODO 3.计算平均耗时
        Long AvgTime = performanceError.getConsumeTime() / count;
        return new Result(ResultEnum.REQUEST_SUCCESS, AvgTime);
    }

    @Override
    public Result getAvgByTypeAndDate(String projectName, String type, String dataType) {
        //需要看每一个属性的每一个时间范围内的均值
        //项目名

        //type : 123为某项性能,
        //datatype: 123为日,月年

        //每一个时间端的均值
        String field = null;
        switch (type) {
            //FP
            case "1":
                field = "first_paint";
                break;
            case "2":
                field = "first_contentful_paint";
                break;
            case "3":
                field = "dom_content_loaded_time";
                break;
            case "4":
                field = "dns";
                break;
            case "5":
                field = "fid";
                break;
            case "6":
                field = "largest_contentful_paint";
                break;
            case "7":
                field = "first_meaningful_paint";
                break;
            case "8":
                field = "long_task";
                break;
            case "9":
                field = "time_to_interactive";
                break;
            case "10":
                field = "load_time";
                break;
            default:
                break;
        }

        List<PerformanceError>  performanceErrors = null;
        switch (dataType) {
            //日
            case "1":
                 performanceErrors = selectHourType(projectName,field);
                break;
            case "2":
                performanceErrors = selectDayType(projectName,field);
                break;
            case "3":
                performanceErrors = selectMonthType(projectName,field);
                break;
            default:
                break;
        }

        //TODO
        return new Result(performanceErrors);


    }

    @Override
    public Map<String,Object> getFP(String projectName) {
        QueryWrapper<PerformanceError> qw = new QueryWrapper<>();
        LocalDateTime time = LocalDateTime.now();
        qw.eq("project_name",projectName)
                .le("date", time)
                .ge("date", time.plusDays(-7));
        Long ThisWeekCount = performanceErrorMapper.selectCount(qw);
        qw.select("SUM(first_paint) AS first_paint");
        PerformanceError performanceError = performanceErrorMapper.selectOne(qw);
        Long ThisWeekFirstPaint = 0L;
        if(performanceError!=null){
            ThisWeekFirstPaint = performanceError.getFirstPaint();
        }
        double ThisWeekAvgTime = 0;
        if (ThisWeekCount != 0){
             ThisWeekAvgTime = 1.000*ThisWeekFirstPaint/ThisWeekCount;
        }
        String  str = String.format("%.2f",ThisWeekAvgTime);
        ThisWeekAvgTime = Double.parseDouble(str);

        QueryWrapper<PerformanceError> qw1 = new QueryWrapper<>();
        qw1.eq("project_name",projectName)
                .le("date", time.plusDays(-7))
                .ge("date", time.plusDays(-14));

        qw1.select("SUM(first_paint) AS first_paint");
        PerformanceError performanceError1 = performanceErrorMapper.selectOne(qw1);
        Long LastWeekFirstPaint = 0L ;
        LastWeekFirstPaint = performanceError1.getFirstPaint();


        Map<String,Object> result = new HashMap<>();
        result.put("count",ThisWeekCount);
        result.put("ThisWeekAvgTime",ThisWeekAvgTime);
        result.put("ThisWeekFirstPaint",ThisWeekFirstPaint);
        result.put("LastWeekFirstPaint",LastWeekFirstPaint);
        //计算同比上周增长率
        Double rate = 100.0;
        if(LastWeekFirstPaint!=0){
            rate = (double) ((ThisWeekFirstPaint - LastWeekFirstPaint) / LastWeekFirstPaint * 100);
        }
        result.put("rate",rate);
        return result;
    }

    private List<PerformanceError> selectHourType(String projectName,String field) {

        List<PerformanceError> data = new ArrayList<>();
        PerformanceError vo = null;

        QueryWrapper<PerformanceError> qw = new QueryWrapper<>();

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();


        Long count = null;

        //往前查询

        for (int i = 0; i < 4; i++) {
            vo = new PerformanceError();
            qw = new QueryWrapper<>();
            qw.eq("project_name",projectName)
                    .isNotNull(field).lambda()
                    .le(PerformanceError::getDate,time)
                    .ge(PerformanceError::getDate,time.plusHours(-6));

            count = performanceErrorMapper.selectCount(qw);
            vo.setConsumeTime(count);
            vo.setConsumeTime(0L);

            if (count != 0){
                qw.clear();
                qw.eq("project_name",projectName)
                        .isNotNull(field)
                        .select("SUM("+field+") AS consumeTime")
                        .le("date",time)
                        .ge("date",time.plusHours(-6));
                PerformanceError selectOne = performanceErrorMapper.selectOne(qw);
                Long consumeTime = selectOne.getConsumeTime();
                vo.setConsumeTime(consumeTime / count / 1000);
            }

            vo.setDateStr(time.plusHours(-6).getHour() + "时-" + time.getHour() + "时");
            data.add(vo);

            time  = time.plusHours(-6);
        }

        return data;

    }

    private List<PerformanceError> selectDayType(String projectName,String field) {

        List<PerformanceError> data = new ArrayList<>();
        PerformanceError vo = null;

        QueryWrapper<PerformanceError> qw = new QueryWrapper<>();

        LocalDateTime time = LocalDateTime.now();


        Long count = null;

        //往前查询

        for (int i = 0; i < 4; i++) {
            vo = new PerformanceError();
            qw = new QueryWrapper<>();
            qw.eq("project_name",projectName)
                    .isNotNull(field).lambda()
                    .le(PerformanceError::getDate,time)
                    .ge(PerformanceError::getDate,time.plusDays(-7));

            count = performanceErrorMapper.selectCount(qw);
            vo.setConsumeTime(count);
            vo.setConsumeTime(0L);
            if (count != 0){
                qw.clear();
                qw.eq("project_name",projectName)
                        .isNotNull(field)
                        .select("SUM("+field+") AS consumeTime")
                        .le("date",time)
                        .ge("date",time.plusDays(-7));
                PerformanceError selectOne = performanceErrorMapper.selectOne(qw);
                Long consumeTime = selectOne.getConsumeTime();
                vo.setConsumeTime(consumeTime / count / 1000);
            }


            vo.setDateStr(time.plusDays(-7).getDayOfMonth() + "日-" + time.getDayOfMonth() + "日");
            data.add(vo);

            time  = time.plusDays(-7);
        }

        return data;

    }

    private List<PerformanceError> selectMonthType(String projectName,String field) {

        List<PerformanceError> data = new ArrayList<>();
        PerformanceError vo = null;

        QueryWrapper<PerformanceError> qw = new QueryWrapper<>();

        LocalDateTime time = LocalDateTime.now();


        Long count = null;

        //往前查询

        for (int i = 0; i < 4; i++) {
            vo = new PerformanceError();
            qw = new QueryWrapper<>();
            qw.eq("project_name",projectName)
                    .isNotNull(field).lambda()
                    .le(PerformanceError::getDate,time)
                    .ge(PerformanceError::getDate,time.plusMonths(-3));

            count = performanceErrorMapper.selectCount(qw);
            vo.setConsumeTime(count);
            vo.setConsumeTime(0L);
            if (count != 0){
                qw.clear();
                qw.eq("project_name",projectName)
                        .isNotNull(field)
                        .select("SUM("+field+") AS consumeTime")
                        .le("date",time)
                        .ge("date",time.plusMonths(-3));
                PerformanceError selectOne = performanceErrorMapper.selectOne(qw);
                Long consumeTime = selectOne.getConsumeTime();
                vo.setConsumeTime(consumeTime / count / 1000);
            }


            vo.setDateStr(time.plusMonths(-3).getMonthValue() + "月-" + time.getMonthValue() + "月");
            data.add(vo);

            time  = time.plusMonths(-3);
        }
        return data;
    }




}
