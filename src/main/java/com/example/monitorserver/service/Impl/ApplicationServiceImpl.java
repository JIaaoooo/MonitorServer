package com.example.monitorserver.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.ApplicationMapper;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.MessageService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 申请服务实现层
 * @author: Jiao
 * @create: 2022-08-09 17:17
 * @version: 1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ProjectService projectService;

    @Lazy
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserProjectService userProjectService;


    @Override
    public Result releaseApp(Application application) {
        String ID = IdUtil.simpleUUID();
        application.setApplicationId(ID);
        application.setDate(LocalDateTime.now());
        int count = 0;

        // TODO 邀请发布者
        if(application.getType()==2){
            Message message =new Message()
                    .setUserId(application.getUserId())
                    .setApplicationId(ID);
            messageService.addMessage(message);
            application.setStatus(1);
        }
        // TODO 申请成为监控者和删除项目，都要向发布者们发送申请

        else{
            if(application.getType()==1){
                application.setStatus(1);
            }
            // TODO 2.查询该项目的所有发布者
            Map<String,Object> condition = new HashMap<>();
            condition.put("project_id",application.getProjectId());
            Result select = userProjectService.select(condition);
            List<UserProject> lists= (List<UserProject>) select.getData();
            Iterator<UserProject> iterator = lists.iterator();
            while(iterator.hasNext()){
                count ++;
                UserProject next = iterator.next();
                // TODO 成功获取得到userId
                String userId = next.getUserId();
                // TODO 3.存入消息
                Message message =new Message()
                        .setUserId(userId)
                        .setApplicationId(ID);
                messageService.addMessage(message);
            }
        }
        if(application.getType()==3){
            application.setStatus(count);
        }
        applicationMapper.insert(application);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result updateApp(Application application) {
        //TODO 1.更新Application表中的数据
        UpdateWrapper<Application> wrapper = new UpdateWrapper<>();
        wrapper.eq("application_id",application.getApplicationId());

        applicationMapper.update(application,wrapper);
        if(application.getStatus()==0){
            int type  = application.getType();
            // TODO 1.申请监控
             if (type == 1){
                 // 将申请人 与 项目id 信息存入t_project_user表
                 UserProject userProject = new UserProject()
                         .setType(2)
                         .setProjectId(application.getProjectId())
                         .setUserId(application.getApplicantId());
                 userProjectService.add(userProject);
             }
             // TODO 2.邀请成为发布者
             if (type==2){
                 UserProject userProject = new UserProject()
                         .setType(1)
                         .setProjectId(application.getProjectId())
                         .setUserId(application.getUserId());
                 userProjectService.add(userProject);
             }
            //TODO 3.  删除

            if(type==3){

                //删除项目
                // TODO 通过项目id去获取项目信息，包括发布者
                Map<String,Object> condition = new HashMap<>();
                condition.put("project_id",application.getProjectId());
                Result byCondition = projectService.getByCondition(condition);
                List<Project> lists = (List<Project>) byCondition.getData();
                Project project = lists.iterator().next();
                Data data = new Data()
                        .setUserId(project.getUserId())
                                .setProjectName(project.getProjectName());
                projectService.deleteProject(data);
                //删除redis首页缓存
                if(Boolean.TRUE.equals(redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()))){
                    redisTemplate.delete(RedisEnum.INDEX_KEY.getMsg());
                }
            }
        }
        // TODO 更新message表中的handle为1
        Message message = new Message()
                .setHandle(application.getHandle())
                        .setApplicationId(application.getApplicationId())
                                .setUserId(application.getUserId());
        messageService.update(message);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result selectApp(Map<String,Object> condition) {
        QueryWrapper<Application> wrapper = new QueryWrapper<>();
        Iterator<String> iterator = condition.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Object value = condition.get(key);
            wrapper.eq(key,value);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS,applicationMapper.selectList(wrapper));
    }

    @Override
    public Result deleteAppli(Map<String,Object> condition) {
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        Iterator<String> iterator = condition.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Object value = condition.get(key);
            queryWrapper.eq(key,value);
        }
        applicationMapper.delete(queryWrapper);
        return null;
    }


}
