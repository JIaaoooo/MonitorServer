package com.example.monitorserver.aop;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.UserProjectMapper;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.utils.MapBeanUtil;
import com.example.monitorserver.utils.MybatisConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class MonitorAop {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 判断是否具有权限
     * @param pjp 程序运行追踪
     * @return  是否允许通过
     */
    @Around(
            "execution(* com.example.monitorserver.controller.StatisticsController.*(..)) ||" +
            "execution(* com.example.monitorserver.controller.LogController.*(..)) ||"+
            "execution(* com.example.monitorserver.controller.AcceptController.*(..))")
    public Result jurisdiction(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("监控权限AOP");
        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        int position = user.getPosition();
        //TOOD 1.为管理员，可直接通过
        if (position==9){
            return (Result) pjp.proceed();
        }
        String userId = user.getUserId();
        //TODO 2.查询user_project表查看时候具有权限
        //////////////////////////////////注意！！！！！！！！！！！！！！！！！问题
        String project_names = String.valueOf(request.getParameterValues("project_name"));
        //TODO 3.通过项目名获得项目id
        Map<String,Object> condition = new HashMap<>();
        condition.put("project_name",project_names);
        Result byCondition = projectService.getByCondition(condition);
        Project data = (Project) byCondition.getData();
        String projectId = data.getProjectId();
        //TODO 4.查看是否具有权限
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("project_id",projectId);
        Result select = userProjectService.select(map);
        if (select.getData() != null){
            return (Result) pjp.proceed();
        }
        return new Result(ResultEnum.REQUEST_FALSE);

    }
}


