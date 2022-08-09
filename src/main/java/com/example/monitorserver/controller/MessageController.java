package com.example.monitorserver.controller;


import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @program: monitor server
 * @description: 申请信息获取层
 * @author: Jiao
 * @create: 2022-08-09 16：07
 */
@RestController
@Slf4j
@RequestMapping(value="/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    public Result getApplication(Long userId){
        return messageService.getApplication(userId);
    }
}
