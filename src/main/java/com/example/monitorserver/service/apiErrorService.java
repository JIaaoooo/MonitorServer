package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.apiError;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public interface apiErrorService extends IService<apiError> {

    /**
     * 在用户发布项目后，在apiError创建对应名称的信息，存储该项目累积的报错、访问量、访问延迟的信息
     * @param apiError api对象
     */
    void insert(apiError apiError);

    /**
     * 通过项目名称，获取各个接口的错误数,平均耗时
     * @param projectName 项目名称
     * @return 返回api实体类
     */
    Result selectMethod(String projectName);

    /**
     * 更新统计信息
     * @param apiError apiError实体类，存储要更新的信息
     */
    void update(apiError apiError);


    /**
     * 获取成功率
     * @param project_name 项目名称
     * @return 返回成功率
     */
    Result getWhole(String project_name);

    /**
     * 获取各个接口的错误率，平均耗时
     * @param project_name  项目名
     * @return 返回前端所需信息
     */
    Result each(String project_name);

    /**
     * 获得该项目名的所有Api错误数量
     * @param projectName 项目名
     * @return 返回js错误数
     */
    Result getApiCount(String projectName);

    /**
     * 获取服务段所有包下的访问量，访问人次，异常数，成功率
     * @return 返回封装集合
     */
    Result getPackageInfor();

    /**
     * 服务端获取某个包下的所有接口的，访问量，访问人次，异常数，成功率
     * @param packageName 包名
     * @return 返回集合
     */
    Result getMethodInfor(String packageName);

    /**
     * 根据年月日返回项目的 成功数，错误数，成功率，访问量，访问人次
     * @param projectName 项目名
     * @param type 时间段选择
     * @return 返回结果集
     */
    Result getApiErrByType(String projectName,String type) throws ExecutionException, InterruptedException;


    /**
     * 获取服务端，该方法下的详细日志信息
     * @param method 方法名
     * @return 返回详细日志信息
     */
    Result getDetail(String method);

}
