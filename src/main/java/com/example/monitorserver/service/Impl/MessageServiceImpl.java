package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.MessageMapper;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.MessageService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 申请信息 服务实现层
 * @author: Jiao
 * @create: 2022-08-09 17:17
 */

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {



    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ApplicationService applicationService;


    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;


    @Override
    public Result getApplication(String userId) {
        //TODO 1.userId是作为接收方 ， 查询该用户下的申请信息ID
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId,userId)
                .select(Message::getApplicationId);
        List<Message> messages = messageMapper.selectList(wrapper);
        //TODO 2.通过applicationId去调用Application获取申请信息,用一个Map集合存储，后返回
        List<Project> projects = new ArrayList<>();

        for (Message message : messages) {
            String applicationId = message.getApplicationId();
            Map<String,Object> condition = new HashMap<>();
            condition.put("application_id",applicationId);
            Result result = applicationService.selectApp(condition);
            List<Application> data = (List<Application>) result.getData();
            Application application = data.iterator().next();
            // TODO 通过申请表中的projectId获取项目信息
            Map<String,Object> condition2 = new HashMap<>();
            condition2.put("project_id",application.getProjectId());
            Result byCondition = projectService.getByCondition(condition2);
            List<Project> projectsList = (List<Project>) byCondition.getData();
            Project project = projectsList.get(0);
            project.setAppliType(application.getType());
            project.setAppliId(application.getApplicationId());
            // 给予前端申请人名字
            Result byUserID = userService.getByUserID(application.getApplicantId());
            User user = (User) byUserID.getData();
            project.setAppiUser(user.getUsername());
            projects.add(project);

            //TODO 3.将该信息设置为已读
            message.setStatus(1);
            update(message);
        }
        // TODO 3 将用户表中message数据重置为0
        Result result = userService.getByUserID(userId);
        User user = (User) result.getData();
        user.setMessage(0);
        userService.update(user);
        return new Result(ResultEnum.APPLICATION_MESSAGE, projects);
    }

    @Override
    public Result addMessage(Message message) {
        //TODO 1.将关联信息存入表中
        messageMapper.insert(message);
        //TODO 2.在对应用户信息中，message信息+1
        //TODO 2.1通过用户ID获取用户对象
        HashMap<String, Object> map = new HashMap<>();
        // TODO 2.2 返回的List集合中仅有一个user对象，获取该对象，修改其message值
        Result result = userService.getByUserID(message.getUserId());
        User user = (User) result.getData();
        int messageCount = user.getMessage();
        user.setMessage(++messageCount);
        //TODO 2.3 更新user
        userService.update(user);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public void update(Message message) {
        UpdateWrapper<Message> up = new UpdateWrapper<>();
        up.eq("id",message.getId());
        messageMapper.update(message,up);
    }


}
