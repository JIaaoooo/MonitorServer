package com.example.monitorserver.service;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-11 16:01
 **/
public interface SendEmailService {
     Boolean sendSimpleEmail(String subject,String text,String projectName);
}
