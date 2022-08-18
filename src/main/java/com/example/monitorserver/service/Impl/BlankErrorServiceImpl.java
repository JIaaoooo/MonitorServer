package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.BlankErrorMapper;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.po.BlankError;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: monitor server
 * @description: 白屏 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */

@Service
public class BlankErrorServiceImpl extends ServiceImpl<BlankErrorMapper,BlankError> implements BlankErrorService {


    @Autowired
    private BlankErrorMapper blankErrorMapper;

    @Override
    public Result insert(BlankError blankError) {
        blankError.setDate(LocalDateTime.now());
        blankErrorMapper.insert(blankError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Long getBlankCount(String  projectName) {
        QueryWrapper<BlankError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        Long count = blankErrorMapper.selectCount(queryWrapper);
        return count;
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

    private List<BlankError> getJsErrHourCount(String projectName) {
        String pattern = "yyyy-MM-dd HH";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<BlankError> data = new LinkedList<>();
        BlankError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();
        time = time.plusHours(1);

        //往前查询
        LambdaQueryWrapper<BlankError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 6; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(BlankError::getProjectName,projectName)
                    .le(BlankError::getDate,time)
                    .ge(BlankError::getDate,time.plusHours(-4));

            count = blankErrorMapper.selectCount(lqw);
            vo = new BlankError();
            vo.setCount(count);
            vo.setDateStr(time.plusHours(-4).getHour() + "时-" + time.getHour()+"时");
            data.add(vo);
            sum += count;

            time  = time.plusHours(-4);
        }

        return getPercent(data, sum);
    }

    private List<BlankError> getJsErrDayCount(String projectName) {
        String pattern = "yyyy-MM-dd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<BlankError> data = new LinkedList<>();
        BlankError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();

        //往前查询
        LambdaQueryWrapper<BlankError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(BlankError::getProjectName,projectName)
                    .le(BlankError::getDate,time)
                    .ge(BlankError::getDate,time.plusDays(-7));

            count = blankErrorMapper.selectCount(lqw);
            vo = new BlankError();
            vo.setCount(count);
            vo.setDateStr(time.plusDays(-7).getDayOfMonth() + "日-" + time.getDayOfMonth()+"日");
            data.add(vo);
            sum += count;

            time  = time.plusDays(-7);
        }

        return getPercent(data, sum);
    }

    private List<BlankError> getJsErrMonthCount(String projectName) {
        String pattern = "yyyy-MM";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<BlankError> data = new LinkedList<>();
        BlankError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();

        //往前查询
        LambdaQueryWrapper<BlankError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 12; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(BlankError::getProjectName,projectName)
                    .le(BlankError::getDate,time)
                    .ge(BlankError::getDate,time.plusMonths(-7));

            count = blankErrorMapper.selectCount(lqw);
            vo = new BlankError();
            vo.setCount(count);
            vo.setDateStr(time.plusMonths(-1).getMonthValue() + "月-" + time.getMonthValue()+"日");
            data.add(vo);
            sum += count;

            time  = time.plusMonths(-1);
        }

        return getPercent(data, sum);
    }

    @NotNull
    private List<BlankError> getPercent(List<BlankError> data, Long sum) {
        double percent;
        for (BlankError datum : data) {
            percent = datum.getCount() * 100.0 / sum ;
            String  str = String.format("%.2f",percent);
            percent = Double.parseDouble(str);
            datum.setPercent(percent);
        }
        return data;
    }
}
