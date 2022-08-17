package com.example.monitorserver.controller;

import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 项目可执行层
 * @author: Jiao
 * @create: 2022-08-09 12:29
 */
@RestController
@RequestMapping(value = "/project",produces = "application/json;charset=UTF-8")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class ProjectController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 获取项目信息（在用户登陆后，展示已经审批、未冻结通过的项目）
     * @param current 当前页
     * @param position 当前用户的权限 若为0，普通用户，不能查看审核未通过项目   若为9，超级管理员，则可以全部显示  （如果不传position也可以直接传整个user信息）
     * @return
     */
    @GetMapping("/pageProject/{current}/{position}")
    @Secret
    public Result getPageProject(@PathVariable int current, @PathVariable int position){
        //判断缓存是否存在首页
        List<Project> projects = null;
        if(current==1&& Boolean.TRUE.equals(redisTemplate.hasKey(RedisEnum.TOKEN_EXITS.getMsg()))&&position==0){
            //前端要获取首页并且，首页信息加载在缓存，直接再换存中获取
            projects = (List<Project>) redisTemplate.opsForList().rightPop(RedisEnum.TOKEN_EXITS.getMsg());
            return new Result(ResultEnum.SELECT_PAGE,projects);
        }
        return projectService.getPageProject(current, 10, position);
    }

    /**
     *获取所有项目信息
     * @return
     */
    @GetMapping("/allProject")
    @Secret
    public Result getAllProject(){
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        return projectService.getAllProject(user.getPosition());
    }


    /**
     * 模糊、条件查询项目信息
     * @param data 前端信息集合
     * @return 返回项目信息
     */
    @PostMapping("/getByCondition")
    @Secret
    public Result getByCondition(@RequestBody Data data){
        Map<String,Object> map = new HashMap<>();
        map.put("project_name",data.getProjectName());
        return projectService.getByCondition(map);
    }

    /**
     * 存储项目信息（可是项目仍需要管理员的审批）
     * @param project  传入项目信息
     * @return 返回操作结果
     */
    @PostMapping("/saveProject")
    @Secret
    public Result saveProject(@RequestBody Project project){
        //生成唯一id
        String ID = IdUtil.simpleUUID();
        project.setProjectId(ID);
        String userId = project.getUserId();
        Result byUserID = userService.getByUserID(userId);
        User user = (User) byUserID.getData();
        project.setUsername(user.getUsername());
        return projectService.saveProject(project);
    }

    /**
     * 更新项目信息 （管理员更新项目的审批情况，以及发布者自己修改项目时使用）
     * @param project 根据项目URL，更新
     * @return 返回操作结果
     */
    @PostMapping("/update")
    @Secret
    public Result Update(@RequestBody Project project){
        // TODO 两种，管理员为项目更新，用户给项目更新
        // TODO 1.获取当前操作对象的权限
        String token = request.getHeader("Authorization");
        // TODO 2.遍历已存的token，获取权限
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        int position  = user.getPosition();
        Project next = new Project();
        if(position == 9){
            Map<String,Object> condition = new HashMap<>();
            condition.put("project_id",project.getProjectId());
            Result byCondition = projectService.getByCondition(condition);
            List<Project> projects = (List<Project>) byCondition.getData();
            next = projects.iterator().next();
            int status = Integer.parseInt(project.getPass());
            next.setStatus(status);
            if (status==-1){
                LocalDateTime dateTime = project.getTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
                next.setUnsealDate(dateTime);
            }
        }

        return projectService.updateProject(next,position);
    }


}
