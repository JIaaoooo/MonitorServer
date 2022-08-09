package com.example.monitorserver.controller;

import com.example.monitorserver.Mapper.ProjectMapper;
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
     * 获取项目信息
     * @param current 当前页
     * @param max 每页最大显示
     * @param position 当前用户的权限 若为0，普通用户，不能查看审核未通过项目   若为9，超级管理员，则可以全部显示
     * @return
     */
    public Result getPageProject(int current,int max,int position){
        return projectService.getPageProject(current,max,position);
    }

    public Result getByCondition(Map<String,Object> map){
        return projectService.getByCondition(map);
    }

    public Result saveProject(Project project){
        return projectService.saveProject(project);
    }

    public Result update(Project project){
        return projectService.updateProject(project);
    }


}
