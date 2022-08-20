package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.ResourceErrorMapper;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.ResourceErrorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 资源异常 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */
@Service
public class ResourceErrorServiceImpl extends ServiceImpl<ResourceErrorMapper, ResourceError> implements ResourceErrorService {

    @Autowired
    private ResourceErrorMapper resourceErrorMapper;

    @Override
    public Result insert(ResourceError resourceError) {
        resourceError.setDate(LocalDateTime.now());
        resourceErrorMapper.insert(resourceError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }


    @Override
    public Result getFileNameByProject(String projectName) {
        //需要获取tag标签下的所有的路径,但是需要distinct

        //首先需要获取项目存在的错误
        QueryWrapper<ResourceError> qw = new QueryWrapper<>();
        qw.select("distinct tagname").lambda().eq(ResourceError::getProjectName, projectName);
        List<ResourceError> resourceErrors = resourceErrorMapper.selectList(qw);

        List[] array = new List[resourceErrors.size()];


        //获取了每一个的tag,现在需要获取tag下的所有的错误的文件路径,需要distinct
        for (int i = 0; i < resourceErrors.size(); i++) {
            qw = new QueryWrapper<>();
            qw.select("distinct filename,tagname").lambda()
                    .eq(ResourceError::getTagname, resourceErrors.get(i).getTagname())
                    .eq(ResourceError::getProjectName, projectName);
            array[i] = resourceErrorMapper.selectList(qw);
        }

        return new Result(array);
    }

    @Override
    public Result getCountByProject(String projectName) {
        //TODO
        //需要获取tag标签下的所有的路径,但是需要distinct

        //首先需要获取项目存在的错误
        QueryWrapper<ResourceError> qw = new QueryWrapper<>();
        qw.select("distinct tagname").lambda().eq(ResourceError::getProjectName, projectName);
        List<ResourceError> resourceErrors = resourceErrorMapper.selectList(qw);

        qw = new QueryWrapper<>();
        qw.lambda().eq(ResourceError::getProjectName,projectName);
        Long sum = 0L;
        sum = resourceErrorMapper.selectCount(qw);
        Long count = 0L;
        List<ResourceError> listVo = new LinkedList<>();

        ResourceError vo = null;
        for (ResourceError resourceError : resourceErrors) {
            qw = new QueryWrapper<>();
            qw.lambda().eq(ResourceError::getTagname, resourceError.getTagname())
                    .eq(ResourceError::getProjectName, projectName);
            count = resourceErrorMapper.selectCount(qw);

            vo = new ResourceError();
            vo.setCount(count).setPercent(getDouble(count * 100.0 / sum)).setTagname(resourceError.getTagname());

            listVo.add(vo);
        }
        return new Result(listVo);

    }

    @Override
    public Result getErrByType(String projectName, String type,String option) {
        String tag = null;
        switch (option) {

            case "1":
                tag = "link";
                break;
            case "2":
                tag = "script";
                break;
            case "3":
                tag = "img";
                break;
            case "4":
                tag = "object";
                break;
            default:
                break;
        }
        switch (type) {
            case "1":
                return new Result(getErrHourCount(projectName,tag));
            case "2":
                return new Result(getErrDayCount(projectName,tag));
            case "3":
                return new Result(getErrMonthCount(projectName,tag));
            default:
                return null;
        }
    }

    private List<ResourceError> getErrHourCount(String projectName,String option) {
        String pattern = "yyyy-MM-dd HH";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<ResourceError> data = new LinkedList<>();
        ResourceError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();
        time = time.plusHours(1);

        //往前查询
        LambdaQueryWrapper<ResourceError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(ResourceError::getProjectName,projectName)
                    .eq(ResourceError::getTagname,option)
                    .le(ResourceError::getDate,time)
                    .ge(ResourceError::getDate,time.plusHours(-6));

            count = resourceErrorMapper.selectCount(lqw);
            vo = new ResourceError();
            vo.setCount(count);
            vo.setDateStr(time.plusHours(-6).getHour() + "时-" + time.getHour()+"时");
            data.add(vo);
            sum += count;
            time  = time.plusHours(-6);
        }

        return getPercent(data, sum);
    }

    private List<ResourceError> getErrDayCount(String projectName,String option) {
        String pattern = "yyyy-MM-dd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<ResourceError> data = new LinkedList<>();
        ResourceError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();

        //往前查询
        LambdaQueryWrapper<ResourceError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(ResourceError::getProjectName,projectName)
                    .eq(ResourceError::getTagname,option)
                    .le(ResourceError::getDate,time)
                    .ge(ResourceError::getDate,time.plusDays(-7));

            count = resourceErrorMapper.selectCount(lqw);
            vo = new ResourceError();
            vo.setCount(count);
            vo.setDateStr(time.plusDays(-7).getDayOfMonth() + "时-" + time.getDayOfMonth()+"时");
            data.add(vo);
            sum += count;

            time  = time.plusDays(-7);
        }

        return getPercent(data, sum);
    }

    private List<ResourceError> getErrMonthCount(String projectName,String option) {
        String pattern = "yyyy-MM";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询12个月,所以需要查询到12个数据进行返回
        List<ResourceError> data = new LinkedList<>();
        ResourceError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();


        //往前查询
        LambdaQueryWrapper<ResourceError> lqw = null;
        Long count = null;
        Long sum = 0L;

        for (int i = 0; i < 4; i++) {

            lqw = new LambdaQueryWrapper<>();
            lqw.eq(ResourceError::getProjectName, projectName)
                    .eq(ResourceError::getTagname,option)
                    .le(ResourceError::getDate, time)
                    .ge(ResourceError::getDate, time.plusMonths(-3));

            count = resourceErrorMapper.selectCount(lqw);
            vo = new ResourceError();
            vo.setCount(count);
            vo.setDateStr(time.plusMonths(-3).getMonthValue() + "月-" + time.getMonthValue() + "月");
            data.add(vo);
            sum += count;

            time = time.plusMonths(-3);
        }
        return getPercent(data, sum);
    }

    private double getDouble(double percent) {
        String  str = String.format("%.2f",percent);
        percent = Double.parseDouble(str);
        return percent;
    }

    @NotNull
    private List<ResourceError> getPercent(List<ResourceError> data, Long sum) {
        double percent;
        for (ResourceError datum : data) {
            percent = datum.getCount() * 100.0 / sum ;
            String  str = String.format("%.2f",percent);
            percent = Double.parseDouble(str);
            datum.setPercent(percent);
        }
        return data;
    }

    @Override
    public Result getResourceCount(String projectName) {
        QueryWrapper<ResourceError> queryWrapper = new QueryWrapper<>();
        LocalDateTime time = LocalDateTime.now();
        queryWrapper.eq("project_name",projectName)
                .le("date", time)
                .ge("date", time.plusDays(-7));
        Long ThisWeek = resourceErrorMapper.selectCount(queryWrapper);

        queryWrapper.clear();
        queryWrapper.eq("project_name",projectName)
                .le("date", time.plusDays(-7))
                .ge("date", time.plusDays(-14));
        Long LastWeek = resourceErrorMapper.selectCount(queryWrapper);
        Map<String,Object> result = new HashMap<>();
        result.put("ThisWeek",ThisWeek);
        result.put("LastWeek",LastWeek);
        return new Result(result);
    }


}
