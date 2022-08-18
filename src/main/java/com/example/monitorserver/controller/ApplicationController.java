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
import java.util.HashMap;
import java.util.List;
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
     * 一：申请监控（前端发送申请类型 1，项目名）  二：邀请发布者（前端发送申请类型 2 ，项目名） 三：删除项目（类型3 ， 项目名）
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

        return applicationService.releaseApp(application);
    }

    /**
     * 用户登录处理信息，后执行,前端传值：applicationId status，根据status判断（0同意，-1拒绝，1待审核）
     * @param application 申请对象
     * @return 结果返回集
     */
    @PostMapping("/update")
    @Secret
    public Result  updateApp(@RequestBody Application application){
        Map<String,Object> condition = new HashMap<>();
        condition.put("application_id",application.getApplicationId());
        Result result = applicationService.selectApp(condition);
        List<Application> list = (List<Application>) result.getData();
        Application application1 = list.get(0).setNumber(application.getNumber());
        return applicationService.updateApp(application1);
    }

    /**
     * 查询获取申请信息
     * @param applicationId 申请列表的id
     * @return 返回该申请信息
     */
    public Result selectApp(String applicationId){
        return null;
    }


    /**
     * 查看我发出的申请信息，查看结果
     * @return 返回申请消息
     */
    @GetMapping("/MySend")
    @Secret
    public Result getMySend(){
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        String userId = user.getUserId();
        // TODO 获取该用户id发布的application
        Map<String,Object> condition = new HashMap<>();
        condition.put("applicant_id",userId);
        return  applicationService.selectApp(condition);
    }
}
