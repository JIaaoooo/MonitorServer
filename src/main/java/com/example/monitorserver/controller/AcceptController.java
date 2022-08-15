package com.example.monitorserver.controller;


import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.service.PerformanceErrorService;
import com.example.monitorserver.service.ResourceErrorService;
import com.example.monitorserver.service.apiErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: monitor server
 * @description: 前端监控接受
 * @author: Jiao
 * @create: 2022-08-13  20：36
 */
@RestController
@RequestMapping("/SDK")
public class AcceptController {

    @Autowired
    HttpServletRequest request;


    @Autowired
    HttpServletResponse response;

    @Autowired
    private apiErrorService apiErrorService;

    @Autowired
    private BlankErrorService blankErrorService;

    @Autowired
    private ResourceErrorService resourceErrorService;

    @Autowired
    private PerformanceErrorService performanceErrorService;

    /**
     * 接受前端SDK信息分类处理
     * @param data 接收信息封装
     */
    @PostMapping
    public Result getSDK(@RequestBody SDK data){
        String type = data.getType();
        switch (type){
            case "JsError":
                apiErrorService.insert((apiError) data.getData());
                break;
            case "BlankError":
                blankErrorService.insert((BlankError) data.getData());
                break;
            case "ResourceError":
                resourceErrorService.insert((ResourceError) data.getData());
                break;
            case "PerformanceError":
                performanceErrorService.insert((PerformanceError) data.getData());
                break;
            default:
                return new Result(ResultEnum.REQUEST_FALSE);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }
}
