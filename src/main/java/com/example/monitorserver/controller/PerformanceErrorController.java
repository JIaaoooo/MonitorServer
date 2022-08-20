package com.example.monitorserver.controller;

import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.PerformanceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.PerformanceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-19 17:05
 **/
@RestController
@RequestMapping("/performance")
@CrossOrigin("http://localhost:3000")
public class PerformanceErrorController {

    @Autowired
    private PerformanceErrorService performanceErrorService;

    /**
     * 性能分析均值,通过性能字段和时间范围
     * @param data 传项目名，时间段选择，性能类型选择
     * @return
     */
    @PostMapping
    @Secret
    public Result getAvgByTypeAndDate(@RequestBody Data data) {
        return performanceErrorService.getAvgByTypeAndDate(data.getProjectName(),data.getType(),data.getDateType());
    }

    /**
     *
     * @param data
     * @return
     */
    @PostMapping("/FP")
    @Secret
    public Result getAvgFP(@RequestBody Data data){
        return performanceErrorService.getFP(data.getProjectName());
    }
}
