package com.example.monitorserver.controller;


import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.apiErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiError")
@CrossOrigin("http://localhost:3000")
public class ApiErrorController {

    @Autowired
    private apiErrorService apiErrorService;

    public Result getServerApi(){
        return null;
    }
}
