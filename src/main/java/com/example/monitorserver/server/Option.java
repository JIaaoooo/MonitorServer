package com.example.monitorserver.server;

import com.alibaba.fastjson.JSON;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.service.LogService;
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
    private LogService logService;


    private static Option option;

    @PostConstruct
    public void init(){
        option = this;
        option.logService  = this.logService;
    }

    public static void MessageHandle(String message){
        Log log = JSON.parseObject(message, Log.class);
        option.logService.createTable();
        option.logService.insert(log);
    }
}
