package com.example.monitorserver.controller;


import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.apiErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/apiError")
@CrossOrigin(origins = "*")
public class ApiErrorController {

    @Autowired
    private apiErrorService apiErrorService;

    @GetMapping("/serverPackage")
    @Secret
    public Result getPackageInfor(){
        return  apiErrorService.getPackageInfor();
    }

    /**
     * 获取服务端的接口日志访问量，访问人次，错误数，错误率
     * @param data 封装项目名
     * @return 返回结果集
     */
    @PostMapping("/serverMethod")
    @Secret
    public Result getMethodInfor(@RequestBody Data data){
        return apiErrorService.getMethodInfor(data.getPackageName());
    }

    /**
     * @param apiError  封装dateType 日1 月2 年3 ， 项目名
     * @return 返回该时间段下的Js错误信息
     */
    @PostMapping("/err")
    @Secret
    public Result getApiErrorByType(@RequestBody apiError apiError) throws ExecutionException, InterruptedException {
        return apiErrorService.getApiErrByType(apiError.getProjectName(),apiError.getDateType());
    }

    /**
     * 获取api各个方法下的错误数，错误率
     * @param data 项目名
     * @return 返回除了没访问量的接口的错误数，错误率
     */
    @PostMapping("/methodError")
    @Secret
    public Result getMethodError(@RequestBody Data data){
        return apiErrorService.selectMethod(data.getProjectName());
    }
}
