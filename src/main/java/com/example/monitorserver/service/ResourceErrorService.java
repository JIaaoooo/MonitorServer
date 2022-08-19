package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: monitor server
 * @description: 资源监控服务层
 * @author: Jiao
 * @create: 2022-08-14 14：07
 */
@Service
public interface ResourceErrorService extends IService<ResourceError> {

    Result insert(ResourceError resourceError);


    Result getFileNameByProject(String projectName);

    Result getCountByProject(String projectName);

    Result getErrByType(String projectName,String type,String option);

    Long getResourceCount(String projectName);
}
