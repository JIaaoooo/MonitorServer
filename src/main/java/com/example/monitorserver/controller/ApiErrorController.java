package com.example.monitorserver.controller;


import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.apiErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiError")
@CrossOrigin("http://localhost:3000")
public class ApiErrorController {

    @Autowired
    private apiErrorService apiErrorService;

    @GetMapping("/serverPackage")
    @Secret
    public Result getPackageInfor(){
        return  apiErrorService.getPackageInfor();
    }

    @PostMapping("/serverMethod")
    @Secret
    public Result getMethodInfor(@RequestBody Data data){
        return apiErrorService.getMethodInfor(data.getPackageName());
    }
}
