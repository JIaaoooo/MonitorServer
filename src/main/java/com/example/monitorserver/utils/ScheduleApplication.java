package com.example.monitorserver.utils;

import com.example.monitorserver.service.LogService;
import com.example.monitorserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
/**
 * @program: Dream
 * @description: 自动执行类
 * @author: Jiao
 * @create: 2022-08-13  12：56
 **/

@SpringBootApplication
@EnableScheduling
public class ScheduleApplication {

    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    /**
     * 五分钟执行一次
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void Scheduled() {
        logService.schedule();
    }

    /**
     * 一分钟执行一次用户信息更新
     */
    @Scheduled(fixedRate = 60 *1000)
    public void UserScheduled(){
        userService.scheduleUpdate();
    }
}