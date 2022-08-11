package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.po.Result;
import org.apache.tomcat.jni.Local;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @program: monitor server
 * @description: 监控 服务处
 * @author: Jiao
 * @create: 2022-08-10 10：00
 */
public interface LogService extends IService<Log> {

    Result createTable();

    Result insert(Log log);

    Result select(HashMap<String,Object> map);

    /**
     * 获取一段时间内的运作项目数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    Result selectProject(LocalDateTime startTime , LocalDateTime endTime);
    /**
     * 每小时自动执行统计
     */
    @Scheduled(fixedRate = 3600 * 1000)
    void HourAutoSum();

    /**
     * 每天自动执行统计
     */
    @Scheduled(fixedRate = 24 * 3600 * 1000)
    void DayAutoSum();

    /**
     * 获得某个时间段下的项目 被访问的包
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param project_id 项目id
     * @return 返回List集合 存有packages
     */
    Result getProjectPackage(LocalDateTime startTime,LocalDateTime endTime,String project_id);

    /**
     * 获得某个时间段下的项目 被访问的方法
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param project_id 项目id
     * @return 返回List集合 存有Methods数据
     */
    Result getProjectMethod(LocalDateTime startTime,LocalDateTime endTime,String project_id);

}
