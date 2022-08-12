package com.example.monitorserver.utils;

import com.example.monitorserver.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@SpringBootApplication
@EnableScheduling
public class ScheduleApplication {

    @Resource
    private LogService logService;

    @Scheduled(fixedRate = 5000)
    public void  Scheduled(){
        logService.schedule();
    }

}
