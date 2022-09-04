package com.example.monitorserver.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.PerformanceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.PerformanceErrorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-19 17:05
 **/
@RestController
@RequestMapping("/performance")
@CrossOrigin("http://localhost:3000")
@Api(tags = "性能监控接口")
public class PerformanceErrorController {

    @Autowired
    private PerformanceErrorService performanceErrorService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 性能分析均值,通过性能字段和时间范围
     * @param data 传项目名项目名projectName，时间段选择dateType，性能类型选择type
     * @return dataStr时间段 ，con
     */
    @PostMapping
    @Secret
    @ApiOperation("通过性能字段和时间范围,获取性能分析数据")
    public Result getAvgByTypeAndDate(@ApiParam(name = "projectName,dateType,type",value = "项目名,时间段选择,错误类型选择",required = true)@RequestBody Data data) {
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+data.getDateType()+data.getType()+"per")){
            List<PerformanceError> performanceError = (List<PerformanceError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+data.getDateType()+data.getType()+"per");
            return new Result(ResultEnum.REQUEST_SUCCESS,performanceError);
        }
        Result result = performanceErrorService.getAvgByTypeAndDate(data.getProjectName(),data.getType(),data.getDateType());
        List<PerformanceError> performanceError = (List<PerformanceError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+data.getDateType()+data.getType()+"per", performanceError);
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+data.getDateType()+data.getType()+"per",1, TimeUnit.HOURS);
        return result;
    }

    /**
     * 获取FP的平均数据，和周同比
     * @param data 项目名projectName
     * @return
     */
    @PostMapping("/FP")
    @Secret
    @ApiOperation("获取FP的平均数据，和周同比")
    public Result getAvgFP(@ApiParam(name = "projectName",value = "项目名",required = true)@RequestBody Data data){
        if(redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"FP")){
            Map<Object, Object> result = redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName() + "FP");
            return new Result(result);
        }
        Map<String, Object> result = performanceErrorService.getFP(data.getProjectName());
        redisTemplate.opsForHash().putAll(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"FP",result);
        return new Result(result);
    }
}
