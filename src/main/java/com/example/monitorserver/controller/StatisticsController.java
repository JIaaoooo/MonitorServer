package com.example.monitorserver.controller;

import com.example.monitorserver.po.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monitor server
 * @description: 统计信息获取
 * @author: Jiao
 * @create: 2022-08-12 14：42
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {


    /**
     * 获取今天各个时段的统计数据
     * @param project_url   项目url
     * @return 返回已过时段的日记统计信息
     */
    public Result getHoursData(String project_url){
        return null;
    }
}
