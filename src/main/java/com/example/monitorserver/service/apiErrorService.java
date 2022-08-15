package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.apiError;
import org.springframework.stereotype.Service;

@Service
public interface apiErrorService extends IService<apiError> {

    /**
     * 在用户发布项目后，在apiError创建对应名称的信息，存储该项目累积的报错、访问量、访问延迟的信息
     * @param apiError api对象
     */
    void insert(apiError apiError);

    /**
     * 通过项目名称，获取接口的错误率，错误数
     * @param projectName 项目名称
     * @return 返回api实体类
     */
    Result select(String projectName);

    /**
     * 更新统计信息
     * @param apiError apiError实体类，存储要更新的信息
     */
    void update(apiError apiError);
}
