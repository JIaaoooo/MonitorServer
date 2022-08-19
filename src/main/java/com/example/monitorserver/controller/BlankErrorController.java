package com.example.monitorserver.controller;


import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.BlankErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: monitor server
 * @description: 白屏错误执行层
 * @author: Jiao
 * @create: 2022-08-19 9：34
 */

@RestController
@RequestMapping("/blankError")
@CrossOrigin("http://localhost:3000")
public class BlankErrorController {

    @Autowired
    private BlankErrorService blankErrorService;


    /**
     * 根据时间粒度获取白屏错误数
     * @param data 项目名，时间粒度
     * @return 返回该项目该时间段下的错误数
     */
    @PostMapping("/brr")
    @Secret
    public Result getBlankErrByType(@RequestBody Data data){
        return blankErrorService.getBlankErrByType(data.getProjectName(), data.getType());
    }
}
