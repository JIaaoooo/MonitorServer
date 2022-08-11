package com.example.monitorserver.controller;

import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: monitor server
 * @description: 申请信息发层
 * @author: Jiao
 * @create: 2022-08-09 16：07
 */

@RestController
@Slf4j
@RequestMapping(value = "/application",produces = "application/json;charset=UTF-8")
public class ApplicationController {


    @Resource
    private ApplicationService applicationService;

    /**
     * 发布申请
     * @param application 申请信息
     * @return 申请处理结果
     */
    public Result  releaseApp(Application application){
        String ID = IdUtil.simpleUUID();
        application.setApplicationId(ID);
        return applicationService.releaseApp(application);
    }

    /**
     * 更新申请状态
     * @param application 申请对象
     * @return 结果返回集
     */
    public Result  updateApp(Application application){
        return applicationService.updateApp(application);
    }

    /**
     * 查询获取申请信息
     * @param applicationId 申请列表的id
     * @return 返回该申请信息
     */
    public Result selectApp(String applicationId){
        return applicationService.selectApp(applicationId);
    }

}
