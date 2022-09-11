package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.monitorserver.mapper.ProjectMapper;
import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.mapper.UserProjectMapper;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.User;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.SendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-11 16:01
 **/
@Service
@Slf4j
public class SendEmailServiceImpl implements SendEmailService {
    @Autowired
    private JavaMailSenderImpl mailSender;

    @Resource
    private UserProjectMapper userProjectMapper;

    @Resource
    private ProjectMapper projectMapper;


    @Resource
    private UserMapper userMapper;

    //注入发件人地址【这里获取的是全局配置里的username】

    @Value("${spring.mail.username}")
    private String from;

    //3.1.4.纯文本邮件发送效果测试【EmailTest】
    @Override
    public Boolean sendSimpleEmail(String subject, String text, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        //通过项目名获取项目id
        String projectId = getProjectIdByProjectName(projectName);

        //用户id集合
        List<String> ids = getUserIdsByProjectId(projectId);

        //拿到用户id下的所有的邮箱地址
        List<String> emails = getEmailsByUserIds(ids);

        for (String email : emails) {
            //发送的对象
            message.setTo(email);

            //标题
            message.setSubject(subject);

            //内容
            message.setText(text);

            //发送
            sendEmail(message);
        }

        return true;
    }


    private String getProjectIdByProjectName(String projectName) {
        //首先查询监控的项目的项目id
        QueryWrapper<Project> projectQueryWrapper = new QueryWrapper<>();

        projectQueryWrapper.select("project_id").lambda().eq(Project::getProjectName, projectName);

        Project project = projectMapper.selectOne(projectQueryWrapper);

        return project.getProjectId();
    }

    private List<String> getUserIdsByProjectId(String projectId) {
        //查询监控的项目的用户id集合
        QueryWrapper<UserProject> userProjectQueryWrapper = new QueryWrapper<>();

        userProjectQueryWrapper.select("user_id").lambda().eq(UserProject::getProjectId, projectId);
        List<UserProject> userProjects = userProjectMapper.selectList(userProjectQueryWrapper);
        List<String> ids = new LinkedList<>();
        for (UserProject userProject : userProjects) {
            ids.add(userProject.getUserId());
        }

        return ids;
    }

    private List<String> getEmailsByUserIds(List<String> userIds) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<String> emails = new LinkedList<>();
        for (String userId : userIds) {
            userQueryWrapper.clear();

            userQueryWrapper.select("phone").lambda().eq(User::getUserId, userId);

            User user = userMapper.selectOne(userQueryWrapper);

            emails.add(user.getPhone());
        }
        return emails;
    }

    private void sendEmail(SimpleMailMessage message) {
        try {
            mailSender.send(message);
            log.debug("邮件发送成功");
        } catch (MailException e) {
            log.debug("邮件发送失败");
            e.printStackTrace();
        }
    }
}
