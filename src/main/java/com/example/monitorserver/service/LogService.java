package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.po.Result;

import java.util.HashMap;

/**
 * @program: monitor server
 * @description: 监控 服务处
 * @author: Jiao
 * @create: 2022-08-10 10：00
 */
public interface LogService extends IService<Log> {

    Result createTable();

    Result select(HashMap<String,Object> map);
}
