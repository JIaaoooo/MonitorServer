package com.example.monitorserver.controller;

import cn.hutool.core.util.IdUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.MessageService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.utils.MapBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    private UserService userService;

    @Autowired
    private MessageService messageService;

    /**
     * 一：申请监控（前端发送申请类型 1，项目名）  二：邀请发布者（前端发送申请类型 2 ，项目名） 三：删除项目（类型3 ， 项目名）
     * @param application 申请信息
     * @return 申请处理结果
     */
    @PostMapping("/releaseApp")
    @Secret
    public Result releaseApp(@RequestBody Application application) throws ExecutionException, InterruptedException {

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
     * 用户登录处理信息，后执行,前端传值：applicationId status，根据status判断（1同意，-1拒绝，0待审核）
     * @param application 申请对象
     * @return 结果返回集
     */
    @PostMapping("/update")
    @Secret
    public Result  updateApp(@RequestBody Application application) throws ExecutionException, InterruptedException {
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);

        Map<String,Object> condition = new HashMap<>();
        condition.put("application_id",application.getApplicationId());
        Result result = applicationService.selectApp(condition);
        List<Application> list = (List<Application>) result.getData();
        Application application1 = list.get(0);
        application1.setHandle(Integer.parseInt(application.getNumber()));

        application1.setUserId(user.getUserId());
        int status = application1.getStatus();
        if (application.getNumber()=="1"){
            status--;
        }
        application1.setStatus(status);
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
     * 查看我发出的申请信息
     * @return 返回接收者，项目名
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
        Result result = applicationService.selectApp(condition);
        List<Application> lists = (List<Application>) result.getData();
        Iterator<Application> iterator = lists.iterator();
        // 结果集封装
        List<Application> ApplicationResult = new ArrayList<>();
        while(iterator.hasNext()){
            Application application = iterator.next();
            //获取项目名
            String projectId = application.getProjectId();
            Map<String,Object> mp = new HashMap<>();
            mp.put("project_id",projectId);
            Result byCondition = projectService.getByCondition(mp);
            List<Project> projectList = (List<Project>) byCondition.getData();
            Project next = projectList.iterator().next();
            String projectName = next.getProjectName();

            String applicationId = application.getApplicationId();
            //通过applicationId去获取申请接收方
            Map<String,Object> map = new HashMap<>();
            map.put("application_id",applicationId);
            Result select = messageService.select(map);
            List<Message> messages = (List<Message>) select.getData();
            Iterator<Message> messageIterator = messages.iterator();
            while (messageIterator.hasNext()){
                Message message = messageIterator.next();
                // 通过id查询用户名
                Result userID = userService.getByUserID(message.getUserId());
                User userResult = (User) userID.getData();
                String username = userResult.getUsername();
                Application appli= new Application()
                        .setProjectName(projectName)
                        .setType(application.getType())
                        .setHandle(message.getHandle())
                        .setUserName(username)
                                .setApplicationId(application.getApplicationId());
                ApplicationResult.add(appli);
            }
        }
        return  new Result(ResultEnum.REQUEST_SUCCESS,ApplicationResult);
    }

    /**
     * 查出已处理信息，或撤回未读信息
     * @param data 传参application_id
     * @return
     */
    @PostMapping("/deleteMySend")
    @Secret
    public Result deleteMySend(@RequestBody Data data){
        Map<String,Object> deleteMap = new HashMap<>();
        deleteMap.put("application_id",data.getApplicationId());
        applicationService.deleteAppli(deleteMap);
        messageService.delete(deleteMap);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }
}
