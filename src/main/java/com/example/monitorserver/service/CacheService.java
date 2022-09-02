package com.example.monitorserver.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Cache;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: 存储、读取缓存
 * @author: Jiao
 * @create: 2022-09-02
 */
@Service
public interface CacheService extends IService<Cache> {

    /**
     * 获取当前时间下的错误数
     * @return
     */
    Cache getTotal();

}
