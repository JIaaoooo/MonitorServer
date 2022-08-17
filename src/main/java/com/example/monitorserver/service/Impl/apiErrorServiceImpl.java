package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.mapper.apiErrorMapper;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.apiErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class apiErrorServiceImpl extends ServiceImpl<apiErrorMapper, apiError> implements apiErrorService {

    @Autowired
    private apiErrorMapper apiErrorMapper;

    @Override
    public void insert(apiError apiError) {
        apiErrorMapper.insert(apiError);
    }

    @Override
    public Result select(String projectName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        return new Result(apiErrorMapper.selectOne(queryWrapper));
    }

    @Override
    public void update(apiError apiError) {
        UpdateWrapper<apiError> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("project_name",apiError.getProjectName());
        apiErrorMapper.update(apiError,updateWrapper);

    }

    @Override
    public Result getWhole(String project_name) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        // TODO 1.获取访问量
        queryWrapper.eq("project_name",project_name);
        Long count = apiErrorMapper.selectCount(queryWrapper);
        // TODO 2.获取异常量
        queryWrapper.ne("exception",null);
        Long exception = apiErrorMapper.selectCount(queryWrapper);
        // TODO 3.计算成功率
        double deaRate  = 1.0*exception/count;
        double rate = 1 - deaRate;
        apiError apiError = new apiError()
                .setRate(rate)
                .setDefeatCount(exception);
        return new Result(ResultEnum.REQUEST_FALSE,apiError);
    }

    @Override
    public Result each(String project_name) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT method")
                .eq("project_name",project_name);
        List<apiError> result = new ArrayList<>();
        // 获取各个接口信息
        List<apiError> apiErrors = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = apiErrors.iterator();
        while(iterator.hasNext()){
            apiError error = iterator.next();
            // 获得接口名称
            String method = error.getMethod();
            QueryWrapper<apiError> qw = new QueryWrapper<>();
            qw.eq("method",method);
            // 计算错误率  获取该接口的api总信息梁
            Long count = apiErrorMapper.selectCount(qw);
            // 获取接口异常数
            qw.ne("exception",null);
            Long exception = apiErrorMapper.selectCount(qw);
            //计算错误率
            double deaRate  = 1.0*exception/count;
            // 获取该接口的平均访问耗时
            QueryWrapper<apiError> qw2 = new QueryWrapper<>();
            qw.eq("method",method)
                    .select("SUM (response_time) AS response_time");
            apiError apiError = apiErrorMapper.selectOne(qw2);
            Long responseTime = apiError.getResponseTime();
            // 计算平均耗时
            Long AvgResponseTime = responseTime / count;
            apiError TheError = new apiError()
                    .setRate(deaRate)
                    .setAvgResponseTime(AvgResponseTime);
            // 存入集合
            result.add(TheError);
        }

        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    @Override
    public Long getApiCount(String projectName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        return apiErrorMapper.selectCount(queryWrapper);
    }


}
