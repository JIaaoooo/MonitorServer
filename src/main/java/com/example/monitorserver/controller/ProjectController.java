package com.example.monitorserver.controller;

import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: monitor server
 * @description: 项目可执行层
 * @author: Jiao
 * @create: 2022-08-09 12:29
 */
@RestController
@RequestMapping(value = "/project",produces = "application/json;charset=UTF-8")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    /**
     * 获取项目信息（在用户登陆后，展示已经审批、未冻结通过的项目）
     * @param current 当前页
     * @param max 每页最大显示
     * @param position 当前用户的权限 若为0，普通用户，不能查看审核未通过项目   若为9，超级管理员，则可以全部显示  （如果不传position也可以直接传整个user信息）
     * @return
     */
    public Result getPageProject(int current,int max,int position){
        return projectService.getPageProject(current,max,position);
    }

    /**
     * 模糊、条件查询项目信息
     * @param map 条件集合（可为json）
     * @return 返回项目信息
     */
    public Result getByCondition(Map<String,Object> map){
        return projectService.getByCondition(map);
    }

    /**
     * 存储项目信息（可是项目仍需要管理员的审批）
     * @param project  传入项目信息
     * @return 返回操作结果
     */
    public Result saveProject(Project project){
        //生成唯一id
        String ID = IdUtil.simpleUUID();
        project.setProjectId(ID);
        return projectService.saveProject(project);
    }

    /**
     * 更新项目信息 （管理员更新项目的审批情况，以及发布者自己修改项目时使用）
     * @param project 根据项目ID，更新
     * @return 返回操作结果
     */
    public Result update(Project project){
        return projectService.updateProject(project);
    }


}
