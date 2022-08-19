package com.example.monitorserver.controller;


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


    @PostMapping("/brr")
    public Result getBlankErrByType(@RequestBody Data data){
        return blankErrorService.getBlankErrByType(data.getProjectName(), data.getType());
    }
}
