package com.example.monitorserver.controller;


import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.utils.MybatisConfig;
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
        Project project = (Project) result.getData();
        Map<String,Object> map = new HashMap<>();
        map.put("project_id",project.getProjectId());
        map.put("type",2);
        Result select = userProjectService.select(map);
        //TODO 返回的是userProject类，内存的是用户ID
        List<UserProject> lists = (List<UserProject>) select.getData();
        Iterator<UserProject> iterator = lists.iterator();
        // TODO 将用户ID 转成用户名 封装成集合返回
        List<String> userNameLists  = new ArrayList<>();
        while (iterator.hasNext()){
            String userId = iterator.next().getUserId();
            Result byUserID = userService.getByUserID(userId);
            User user = (User) byUserID.getData();
            userNameLists.add(user.getUsername());
        }
        return new Result(ResultEnum.REQUEST_SUCCESS,userNameLists);
    }


}
