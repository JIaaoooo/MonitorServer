package com.example.monitorserver.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.ApplicationMapper;
import com.example.monitorserver.po.Message;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.service.MessageService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserProjectService userProjectService;


    @Override
    public Result releaseApp(Application application) {
        String ID = IdUtil.simpleUUID();
        application.setApplicationId(ID);
        int count = 0;

        // TODO 邀请发布者
        if(application.getType()==2){
            Message message =new Message()
                    .setUserId(application.getUserId())
                    .setApplicationId(ID);
            messageService.addMessage(message);
        }
        // TODO 申请成为监控者和删除项目，都要向发布者们发送申请

        else{

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
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId,application.getApplicantId());
        applicationMapper.update(application,wrapper);

        //TODO 2.  删除
        int type  = application.getType();
        if(type==3){
            if(application.getStatus()==0){
                //删除项目
                projectService.deleteProject(application.getProjectId());
                //删除redis首页缓存
                if(Boolean.TRUE.equals(redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()))){
                    redisTemplate.delete(RedisEnum.INDEX_KEY.getMsg());
                }
            }
        }
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result selectApp(String applicationId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId,applicationId);
        Application selectOne = applicationMapper.selectOne(wrapper);
        return new Result(selectOne);
    }


}
