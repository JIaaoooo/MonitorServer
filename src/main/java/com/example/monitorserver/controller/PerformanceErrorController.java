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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-19 17:05
 **/
@RestController
@RequestMapping("/performance")
//@CrossOrigin("http://localhost:3000")
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
    public Result getAvgByTypeAndDate(@RequestBody Data data) {
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"per")){
            List<PerformanceError> performanceError = (List<PerformanceError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"per");
            return new Result(ResultEnum.REQUEST_SUCCESS,performanceError);
        }
        Result result = performanceErrorService.getAvgByTypeAndDate(data.getProjectName(),data.getType(),data.getDateType());
        List<PerformanceError> performanceError = (List<PerformanceError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"per", performanceError);
        return result;
    }

    /**
     * 获取FP的平均数据，和周同比
     * @param data 项目名projectName
     * @return
     */
    @PostMapping("/FP")
    @Secret
    public Result getAvgFP(@RequestBody Data data){

        return performanceErrorService.getFP(data.getProjectName());
    }
}
