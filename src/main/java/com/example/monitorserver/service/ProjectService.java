package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Project;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * @program: monitor server
 * @description: 项目服务层
 * @author: Jiao
 * @create: 2022-08-09 11:38
 * @version: 1.0
 */
@Service
public interface ProjectService extends IService<Project> {
    /**
     * 获取项目信息
     * @param currentPage 当前页
     * @param maxMessage 每页最大显示
     * @param position 当前用户的权限 若为0，普通用户，不能查看审核未通过项目   若为9，超级管理员，则可以全部显示
     * @return
     */
    Result getPageProject(int currentPage, int maxMessage, int position);

    Result getAllProject(int position) throws ExecutionException, InterruptedException;

    /**
     * 通过项目的url获取项目名
     * @param projectUrl 项目url
     * @return 返回项目名
     */
    String getProjectName(String projectUrl);

    /**
     * 通过条件查询项目数据（可模糊查询）
     * @param map 通过键值对的方式
     * @return 返回项目
     */
    Result getByCondition(Map<String,Object> map);

    /**
     * 存储项目
     * @param project 项目信息
     * @return 返回执行结果
     */
    Result saveProject(Project project);

    /**
     * 更新项目信息
     * @param project
     * @return
     */
    Result updateProject(Project project,int position) throws ExecutionException, InterruptedException;

    /**
     * 根据项目id删除
     * @param projectId  项目id
     * @return 返回结果集
     */
    Result deleteProject(Data data) throws ExecutionException, InterruptedException;

}
