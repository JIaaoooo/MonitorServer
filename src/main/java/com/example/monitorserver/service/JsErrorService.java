package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.JsError;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: js监控服务层
 * @author: Jiao
 * @create: 2022-08-14 14：07
 */
@Service
public interface JsErrorService extends IService<JsError> {

    Result insert(JsError jsError);

    Result getUrlError(Data data);

    /**
     * 获得该项目名的所有js错误数量
     * @param projectName 项目名
     * @return 返回js错误数
     */
    Long getJsCount(String projectName);
}
