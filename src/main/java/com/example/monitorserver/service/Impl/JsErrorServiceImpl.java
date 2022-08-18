package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.JsErrorMapper;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.JsErrorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

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
        jsError.setDate(LocalDateTime.now());
        jsErrorMapper.insert(jsError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result getUrlError(Data data) {
        QueryWrapper<JsError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",data.getProjectName())
                .eq("url",data.getUrl());
        Long count = jsErrorMapper.selectCount(queryWrapper);
        JsError jsError = jsErrorMapper.selectOne(queryWrapper);
        jsError.setCount(count);
        return new Result(ResultEnum.REQUEST_SUCCESS,jsError);
    }

    @Override
    public Result getJsErrByType(String projectName, Integer type) {


        //需要根据项目名和类型进行查询日志
        //type:1:日,查询24从现在起前24小时的错误数量,
        //2:月:查询从现在起,4个星期的错误数量
        //3.年:查询从现在起,12个月的错误数

        switch (type) {
            case 1:
                return new Result(getJsErrHourCount(projectName));
            case 2:
                return new Result(getJsErrDayCount(projectName));
            case 3:
                return new Result(getJsErrMonthCount(projectName));
            default:
                return null;
        }
    }

    private List<JsError>getJsErrHourCount(String projectName){
        String pattern = "yyyy-MM-dd HH";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<JsError> data = new LinkedList<>();
        JsError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();
        time = time.plusHours(1);

        //往前查询
        LambdaQueryWrapper<JsError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(JsError::getProjectName,projectName)
                    .le(JsError::getDate,time)
                    .ge(JsError::getDate,time.plusHours(-6));

             count = jsErrorMapper.selectCount(lqw);
             vo = new JsError();
             vo.setCount(count);
             vo.setDateStr(time.plusHours(-6).getHour() + "时-" + time.getHour()+"时");
             data.add(vo);
             sum += count;

             time  = time.plusHours(-6);
        }

        return getPercent(data, sum);
    }


    private List<JsError>getJsErrDayCount(String projectName){
        String pattern = "yyyy-MM-dd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询4个星期,所以需要查询到4个数据进行返回
        List<JsError> data = new LinkedList<>();
        JsError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();


        //往前查询
        LambdaQueryWrapper<JsError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(JsError::getProjectName,projectName)
                    .le(JsError::getDate,time)
                    .ge(JsError::getDate,time.plusDays(-7));

            count = jsErrorMapper.selectCount(lqw);
            vo = new JsError();
            vo.setCount(count);
            vo.setDateStr(time.plusDays(-7).getDayOfMonth() + "日-" + time.getDayOfMonth() + "日");
            data.add(vo);
            sum += count;

            time  = time.plusDays(-7);
        }

        return getPercent(data, sum);
    }



    private List<JsError>getJsErrMonthCount(String projectName){
        String pattern = "yyyy-MM";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询12个月,所以需要查询到12个数据进行返回
        List<JsError> data = new LinkedList<>();
        JsError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();


        //往前查询
        LambdaQueryWrapper<JsError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(JsError::getProjectName,projectName)
                    .le(JsError::getDate,time)
                    .ge(JsError::getDate,time.plusMonths(-3));

            count = jsErrorMapper.selectCount(lqw);
            vo = new JsError();
            vo.setCount(count);
            vo.setDateStr(time.plusMonths(-3).getMonthValue() + "月-" + time.getMonthValue() + "月" );
            data.add(vo);
            sum += count;

            time  = time.plusMonths(-3);
        }

        return getPercent(data, sum);
    }

    @NotNull
    private List<JsError> getPercent(List<JsError> data, Long sum) {
        double percent;
        for (JsError datum : data) {
            percent = datum.getCount() * 100.0 / sum ;
            String  str = String.format("%.2f",percent);
            percent = Double.parseDouble(str);
            datum.setPercent(percent);
        }
        return data;
    }


    @Override
    public Result getUrlErrCountByName(String projectName) {

        //查询项目下各个url的js报错数,和占总数的百分比

        QueryWrapper<JsError> qw = new QueryWrapper<>();


        qw.select("distinct url").lambda().eq(JsError::getProjectName,projectName);
        //获得了项目的所有url,
        List<JsError> jsErrors = jsErrorMapper.selectList(qw);

        //现在需要查询url下的条数即可
        List<JsError> list = new LinkedList<>();

        qw.lambda().eq(JsError::getProjectName,projectName);

        Long sum = jsErrorMapper.selectCount(qw);
        Long count = 0L;
        JsError vo = null;
        for (JsError jsError : jsErrors) {
            qw = new QueryWrapper<>();
            qw.lambda().eq(JsError::getProjectName,projectName).eq(JsError::getUrl,jsError.getUrl());
            count = jsErrorMapper.selectCount(qw);
            sum += count;
            vo = new JsError();
            vo.setUrl(jsError.getUrl()).setCount(count);
            list.add(vo);
        }


        return new Result(getPercent(list, sum));
    }

    @Override
    public Long getJsErrorCount(String projectName) {
        QueryWrapper<JsError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        return jsErrorMapper.selectCount(queryWrapper);
    }
}
