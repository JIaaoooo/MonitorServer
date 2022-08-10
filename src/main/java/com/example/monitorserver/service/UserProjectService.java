package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.UserProject;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: monitor server
 * @description: 用户与项目关系
 * @author: Jiao
 * @create: 2022-08-09 18:37
 */
@Service
public interface UserProjectService extends IService<UserProject> {

    /**
     * 添加权限
     * @param userProject userProject数据  内涵userID  项目ID  权限类型
     * @return 返回操作直接
     */
    Result add(UserProject userProject);

    /**
     * 查询权限
     * @param map 键值对（通过userID获取其在该项目的权限 、 查询该项目的发布人、监控人）
     * @return
     */
    Result select(Map<String,Object> map);
}
