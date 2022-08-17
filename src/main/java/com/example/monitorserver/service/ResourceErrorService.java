package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.ResourceError;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: 资源监控服务层
 * @author: Jiao
 * @create: 2022-08-14 14：07
 */
@Service
public interface ResourceErrorService extends IService<ResourceError> {

    Result insert(ResourceError resourceError);

    /**
     * 获取资源错误数
     * @param projectName 通过项目名获取
     * @return 返回资源错误总数
     */
    Long getResourceCount(String projectName);
}
