package com.example.monitorserver.controller;

import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.JsErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-17 16:38
 **/
@RestController
@RequestMapping("/jsError")
@CrossOrigin("http://localhost:3000")
public class JsErrorController {
    @Autowired
    private JsErrorService jsErrorService;


    /**
     * 获取某个项目的各个时间段下的js报错信息,
     * @param data :报错信息
     * @return :封装了报错信息的结果集
     */
    @PostMapping
    @Secret
    public Result getJsErrByType(@RequestBody Data data) {
        return jsErrorService.getJsErrByType(data.getProjectName(), data.getOption());
    }
}
