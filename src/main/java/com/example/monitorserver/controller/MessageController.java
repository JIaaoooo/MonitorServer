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

    /**
     * 当前用户作为接收方，查询信息库中要发给我的消息（该操作在用户点击进入消息队列后执行）
     * @param userId 当前用户的ID
     * @return 返回其消息  ：返回格式： Application1：xxxx    Application2:xxxx
     */
    public Result getApplication(String userId){
        return messageService.getApplication(userId);
    }
}
