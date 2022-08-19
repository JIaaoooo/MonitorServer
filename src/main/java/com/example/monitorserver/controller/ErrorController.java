package com.example.monitorserver.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.service.JsErrorService;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.service.PerformanceErrorService;
import com.example.monitorserver.service.ResourceErrorService;

import com.example.monitorserver.utils.NettyEventGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @program: monitor server
 * @description: 前端监控接受
 * @author: Jiao
 * @create: 2022-08-13  20：36
 */
@RestController
@RequestMapping("/SDK")
@CrossOrigin(origins = "*")
public class ErrorController {

    @Autowired
    HttpServletRequest request;


    @Autowired
    HttpServletResponse response;

    @Autowired
    private apiErrorService apiErrorService;

    @Autowired
    private JsErrorService jsErrorService;

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
        System.out.println("data = " + data);
        switch (type){
            case "JsError":
                apiError apiError = JSON.parseObject(data.getData(), apiError.class);
                apiErrorService.insert(apiError);
                break;
            case "BlankError":
                BlankError blankError = JSON.parseObject(data.getData(), BlankError.class);
                blankErrorService.insert(blankError);
                break;
            case "ResourceError":
                ResourceError resourceError = JSON.parseObject(data.getData(), ResourceError.class);
                resourceErrorService.insert(resourceError);
                break;
            case "PerformanceError":
                PerformanceError performanceError = JSON.parseObject(data.getData(), PerformanceError.class);
                performanceErrorService.insert(performanceError);
                break;
            default:
                return new Result(ResultEnum.REQUEST_FALSE);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

   /* *//**
     * js错误率
     * @param data 封装项目名
     * @return 返回比例
     */
    @PostMapping("/JsRate")
    @Secret
    public Result JsRate(@RequestBody Data data) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = NettyEventGroup.group;

        Future<Long> JsFuture = group.next().submit(() -> jsErrorService.getJsErrorCount(data.getProjectName()));
        Future<Long> ApiFuture = group.next().submit(() -> apiErrorService.getApiCount(data.getProjectName()));
        Future<Long> BlankFuture = group.next().submit(() -> blankErrorService.getBlankCount(data.getProjectName()));
        Future<Long> ResFuture = group.next().submit(() -> resourceErrorService.getResourceCount(data.getProjectName()));

        Long jsCount = JsFuture.get();
        Long apiCount = ApiFuture.get();
        Long blankCount = BlankFuture.get();
        Long resourceCount = ResFuture.get();
        Long whole = apiCount + blankCount + resourceCount ;
        double rate = 1.000*jsCount / whole;
        String  str = String.format("%.2f",rate );
        rate = Double.parseDouble(str);
        return new Result(ResultEnum.REQUEST_SUCCESS,rate);
    }

    /**
     * js错误数
     * @param data 项目名
     * @return 返回改项目总的js错误
     */
    @PostMapping("/JsCount")
    @Secret
    public Result JsCount(@RequestBody Data data){
        Long jsCount = jsErrorService.getJsErrorCount(data.getProjectName());
        return new Result(ResultEnum.REQUEST_SUCCESS,jsCount);
    }
}
