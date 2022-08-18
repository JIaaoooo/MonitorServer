package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Application;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: monitor server
 * @description: 申请信息 服务处
 * @author: Jiao
 * @create: 2022-08-09 17:17
 */
@Service
public interface ApplicationService extends IService<Application> {
    /**
     * 发布申请
     * @param application 申请对象
     * @return 申请处理结果
     */
    Result releaseApp(Application application);

    /**
     * 根据application_id  更新申请状态 , 用户点击同意在status -1
     * @param application 申请实例化对象
     * @return 返回结果集
     */
    Result updateApp(Application application);

    /**
     * 搜索相关申请信息
     * @param condition 搜索条件信息
     * @return 返回申请信息
     */
    Result selectApp(Map<String,Object> condition);
}
