package com.example.monitorserver.controller;

import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ResourceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-18 15:58
 **/
@RestController
@RequestMapping("/resource")
@CrossOrigin(origins = "*")
public class ResourceErrorController {
    @Autowired
    private ResourceErrorService resourceErrorService;


    /**
     * 通过项目名获取各个错误标签的错误路径,表
     * @param data:项目名
     * @return :封装了错误路径的结果集
     */
    @PostMapping("/brr")
    @Secret
    public Result getFileNameByProject(@RequestBody Data data) {
        return resourceErrorService.getFileNameByProject(data.getProjectName());
    }


    /**
     * 通过项目名各个错误的错误数和比例,图
     * @param data :项目名
     * @return :封装了错误路径的结果集
     */
    @PostMapping("/count")
    @Secret
    public Result getCountByProject(@RequestBody Data data) {
        return resourceErrorService.getCountByProject(data.getProjectName());
    }


    @PostMapping("/err")
    @Secret
    public Result getJsErrByType(@RequestBody ResourceError resourceError) {
        return resourceErrorService.getErrByType(resourceError.getProjectName(), resourceError.getDateType(),resourceError.getType());
    }
}

