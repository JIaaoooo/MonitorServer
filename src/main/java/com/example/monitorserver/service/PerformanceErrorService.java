package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
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

}
