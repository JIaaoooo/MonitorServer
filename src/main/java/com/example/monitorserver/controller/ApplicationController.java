package com.example.monitorserver.controller;

import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.po.Message;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.MessageService;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 申请信息发层
 * @author: Jiao
 * @create: 2022-08-09 16：07
 */

@RestController
@Slf4j
@RequestMapping(value = "/application",produces = "application/json;charset=UTF-8")
@CrossOrigin("http://localhost:3000")
public class ApplicationController {


    @Autowired
    private HttpServletRequest request;
    @Resource
    private ApplicationService applicationService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MessageService messageService;

    /**
     * 发布申请
     * @param application 申请信息
     * @return 申请处理结果
     */
    @PostMapping("/releaseApp")
    @Secret
    public Result releaseApp(@RequestBody Application application){

        String number = application.getNumber();
        application.setType(Integer.parseInt(number));
        // TODO 1.获取当前申请用户的id
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        String userId = user.getUserId();

        // TODO 2.将用户id，项目id存入application
        application.setApplicantId(userId);

        /*// TODO 3.将信息存入message表中
        Message message = new Message()
                .setApplicationId(ID)  //申请表id
                .setUserId(); //接收方id
        messageService.addMessage(message);*/
        return applicationService.releaseApp(application);
    }

    /**
     * 更新申请状态
     * @param application 申请对象
     * @return 结果返回集
     */
    public Result  updateApp(Application application){
        return applicationService.updateApp(application);
    }

    /**
     * 查询获取申请信息
     * @param applicationId 申请列表的id
     * @return 返回该申请信息
     */
    public Result selectApp(String applicationId){
        return applicationService.selectApp(applicationId);
    }

}
