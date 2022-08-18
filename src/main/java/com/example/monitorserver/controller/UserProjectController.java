package com.example.monitorserver.controller;


import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @program: monitor server
 * @description: 用户与项目关系
 * @author: Jiao
 * @create: 2022-08-09 18:36
 */
@RestController
@Slf4j
@RequestMapping(value = "/userproject",produces = "application/json;charset=UTF-8")
@CrossOrigin("http://localhost:3000")
public class UserProjectController {

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * 查询该项目下的监控者
      * @param data 内套项目名
     * @return 返回用户名
     */

    @PostMapping("/viewPermission")
    @Secret
    public Result viewPermission(@RequestBody Data data){
        //TODO 前端给项目名称，获取项目名称对应的ID
        Map<String,Object> condition = new HashMap<>();
        condition.put("project_name",data.getProjectName());
        Result result = projectService.getByCondition(condition);
        List<Project> project = (List<Project>) result.getData();
        Map<String,Object> map = new HashMap<>();
        map.put("project_id",project.iterator().next().getProjectId());
        Result select = userProjectService.select(map);
        //TODO 返回的是userProject类，内存的是用户ID
        List<UserProject> lists = (List<UserProject>) select.getData();
        Iterator<UserProject> iterator = lists.iterator();
        // TODO 将用户ID 转成用户名 封装成集合返回
        List<User> userNameLists  = new ArrayList<>();
        while (iterator.hasNext()){
            UserProject userProject = iterator.next();
            String userId = userProject.getUserId();
            Result byUserID = userService.getByUserID(userId);
            User user = (User) byUserID.getData();
            user.setType(userProject.getType());
            userNameLists.add(user);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS,userNameLists);
    }

    /**
     *查询自己监控\发布的项目
     * @param data 向前端获取用户id
     * @return 返回project实体类
     */
    @PostMapping("/MyProject")
    @Secret
    public Result viewOwnProject(@RequestBody Data data){
        Map<String,Object> condition = new HashMap<>();
        condition.put("user_id",data.getUserId());
        Result select =  userProjectService.select(condition);
        //获取的到该用户下的项目ID
        List<UserProject> list = (List<UserProject>) select.getData();
        if (list.size()==0){
            return new Result(ResultEnum.SELECT_BLANK);
        }
        Iterator<UserProject> iterator = list.iterator();
        Map<String,Object> condition2 = new HashMap<>();
        List<Project> result = new ArrayList<>();
        while(iterator.hasNext()){
            UserProject userProject = iterator.next();
            String projectId = userProject.getProjectId();
            condition2.put("project_id",projectId);
            Result byCondition = projectService.getByCondition(condition2);
            List<Project> listProject = (List<Project>) byCondition.getData();
            if (listProject.size()==0){
                return new Result(ResultEnum.SELECT_BLANK);
            }
            Project next = listProject.iterator().next();
            next.setAppliType(userProject.getType());
            result.add(next);
        }

        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    /**
     * 删除用户的监控权限
     * @param data 封装项目名，用户名
     * @return 返回执行结果
     */
    @PostMapping("/updatePermission")
    @Secret
    public Result updatePermission(@RequestBody Data data){
        // TODO 前端给予项目名称 ， 用户名称
        // TODO 1.获取项目名对应的项目ID
        Map<String,Object> condition1 = new HashMap<>();
        condition1.put("project_name",data.getProjectName());
        Result result = projectService.getByCondition(condition1);
        List<Project> project = (List<Project>) result.getData();
        String projectId = project.iterator().next().getProjectId();
        // TODO 3.信息封装
        UserProject userProject = new UserProject();
        userProject.setProjectId(projectId);
        userProject.setUserId(data.getUserId());
        // TODO 4.删除
        return userProjectService.delete(userProject);
    }
}
