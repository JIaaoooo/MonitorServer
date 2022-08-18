package com.example.monitorserver.controller;


import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.service.PerformanceErrorService;
import com.example.monitorserver.service.ResourceErrorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
@CrossOrigin("http://localhost:3000")
public class ErrorController {

    @Autowired
    HttpServletRequest request;


    @Autowired
    HttpServletResponse response;

    @Autowired
    private com.example.monitorserver.service.apiErrorService apiErrorService;

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
    public Result getSDK(@RequestBody Data data){
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

    /**
     * js错误栈
     * @param data
     * @return
     */
    @PostMapping("/JsRate")
    @Secret
    public Result JsRate(@RequestBody Data data){
        Long apiCount = apiErrorService.getApiCount(data.getProjectName());
        Long blankCount = blankErrorService.getBlankCount(data.getProjectName());
        Long resourceCount = resourceErrorService.getResourceCount(data.getProjectName());
        Long whole = apiCount + blankCount + resourceCount;
        double rate = 1.0*apiCount / whole;
        return new Result(ResultEnum.REQUEST_SUCCESS,rate);
    }
}
