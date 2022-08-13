package com.example.monitorserver.service;

import cn.hutool.core.lang.hash.Hash;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @program: monitor server
 * @description: 统计服务层
 * @author: Jiao
 * @create: 2022-08-11  19:14
 * @version: 1.0
 */

@Service
public interface StatisticsService extends IService<Statistics>  {

    void createTable(String table);

    void insert(Statistics statistics);

    /**
     * 获取项目的访问量峰值 和 访问人数的峰值
     * @param project_id
     * @return
     */
    Result getMax(String project_id);

    Result getHoursData(String project_name);
}
