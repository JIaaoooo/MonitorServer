package com.example.monitorserver.controller;


import cn.hutool.core.lang.hash.Hash;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 申请信息发层
 * @author: Jiao
 * @create: 2022-08-09 16：07
 */
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 获得项目运行的包名
     * @param project_url 项目url
     * @return 返回获取的监控信息
     */
    public Result getProjectPackageName(String project_url){
        return logService.getProjectPackage(null, null, project_url);
    }

    /**
     * 获得项目运行的接口名
     * @param project_url
     * @return
     */
    public Result getProjectMethod(String project_url){
        return logService.getProjectMethod(null,null,project_url);
    }

    /**
     * 获取后台代码的日志下信息  （弄成分页）
     * @param currentPage 当前页
     * @param project_name 项目的name
     * @return 返回日志信息
     */
    public Result getCurrentLog(int currentPage,String project_name){
        return logService.getCurrentLog(currentPage, project_name);
    }

}
