package com.example.monitorserver.controller;


import cn.hutool.core.lang.hash.Hash;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 申请信息发层
 * @author: Jiao
 * @create: 2022-08-09 16：07
 */
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 根据不同的条件返回值  功能繁多：查看该项目的
     * @param condition   键值对  key：条件名   value ：值
     * @return 返回获取的监控信息
     */
    public Result getByCondition(HashMap<String, Object> condition){
        return logService.select(condition);
    }

}
