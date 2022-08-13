package com.example.monitorserver.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/SDK")
public class AcceptController {

    @Autowired
    HttpServletRequest request;


    @Autowired
    HttpServletResponse response;

    @PostMapping
    public void getSDK(@RequestBody String Date){
        System.out.println(Date);
    }
}
