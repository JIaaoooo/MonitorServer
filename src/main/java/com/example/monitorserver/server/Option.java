package com.example.monitorserver.server;

import com.alibaba.fastjson.JSON;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.apiErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @program: monitor server
 * @description: 用于客户端信息传输交通枢纽
 * @author: Jiao
 * @create: 2022-08-12  13：30
 */
@Component
public class Option {

    @Autowired
    private apiErrorService apiErrorService;


    @Autowired
    private ProjectService projectService;

    private static Option option;

    @PostConstruct
    public void init(){
        option = this;
        option.apiErrorService  = this.apiErrorService;
        option.projectService = this.projectService;
    }

    public static void MessageHandle(String message){
        apiError error = JSON.parseObject(message, apiError.class);
        String projectName = option.projectService.getProjectName(error.getProjectUrl());
        error.setPackageName(projectName);
        option.apiErrorService.insert(error);
    }
}
