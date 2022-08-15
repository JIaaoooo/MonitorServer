package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.PerformanceError;
import com.example.monitorserver.po.Result;
import org.springframework.stereotype.Service;


/**
 * @program: monitor server
 * @description: 性能监控服务层
 * @author: Jiao
 * @create: 2022-08-14 14：07
 */
@Service
public interface PerformanceErrorService extends IService<PerformanceError> {

    Result insert(PerformanceError performanceError);

    /**
     * 前端给予项目名、时间，获取直到该时刻的数据
     * @param data 封装类
     * @return 返回截至这段时间的平均值
     */
    Result select(Data data);
}
