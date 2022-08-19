package com.example.monitorserver.controller;

import com.example.monitorserver.annotation.Secret;
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
     * @param performanceError
     * @return
     */
    @PostMapping
    @Secret
    public Result getAvgByTypeAndDate(@RequestBody PerformanceError performanceError) {
        return performanceErrorService.getAvgByTypeAndDate(performanceError.getProjectName(),performanceError.getType(),performanceError.getDateType());
    }
}
