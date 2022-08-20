package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.Result;
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
     * 根据type判断年月日，不同粒度获取
     * @param projectName 项目名
     * @param type 粒度
     * @return 过去时间的错误数
     */
    Result getJsErrByType(String projectName,String type);

    /**
     * 查询项目下各个url的js报错数,和占总数的百分比
     * @param projectName 项目名
     * @return 和占总数的百分比
     */
    Result getUrlErrCountByName(String projectName);

    /**
     * 获取该项目总的js错误数
     * @param projectName 项目名
     * @return 返回总项目数
     */
    Result getJsErrorCount(String projectName);

   /* Result getLastWeekData(String projectName);*/
}
