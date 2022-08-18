package com.example.monitorserver.controller;

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
@CrossOrigin("http://localhost:3000")
public class ResourceErrorController {
    @Autowired
    private ResourceErrorService resourceErrorService;


    /**
     * 通过项目名获取各个错误标签的错误路径
     * @param resourceError:项目名
     * @return :封装了错误路径的结果集
     */
    @PostMapping
    public Result getFileNameByProject(@RequestBody ResourceError resourceError) {
        return resourceErrorService.getFileNameByProject(resourceError.getProjectName());
    }
}
