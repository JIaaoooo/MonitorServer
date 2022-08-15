package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.ResourceErrorMapper;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ResourceErrorService;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @program: monitor server
 * @description: 资源异常 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */
@Service
public class ResourceErrorServiceImpl extends ServiceImpl<ResourceErrorMapper,ResourceError> implements ResourceErrorService {

    @Autowired
    private ResourceErrorMapper resourceErrorMapper;

    @Override
    public Result insert(ResourceError resourceError) {
        MybatisConfig.setDynamicTableName("t_resourceError");
        resourceError.setDate(LocalDateTime.now());
        resourceErrorMapper.insert(resourceError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result getCount(Data data) {
        MybatisConfig.setDynamicTableName("t_resourceError");
        LocalDateTime now = LocalDateTime.now();
        int option = data.getOption();
        LocalDateTime dateTime = null;
        switch (option){
            case 1:
                //获取前半小时的localdatetime格式
                dateTime = now.minusMinutes(30);
                break;
            case 2:
                //获取前一天的
                dateTime = now.minusDays(1);
                break;
            case 3:
                //获取前一个月的时间
                dateTime = now.minusMonths(1);
            default: break;
        }
        QueryWrapper<ResourceError> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("date",dateTime);
        Long count = resourceErrorMapper.selectCount(queryWrapper);
        return new Result(ResultEnum.REQUEST_SUCCESS,count);
    }
}
