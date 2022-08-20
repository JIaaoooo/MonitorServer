package com.example.monitorserver.aop;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.Constants;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.exception.SystemException;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.utils.AesUtil;
import com.example.monitorserver.utils.MapBeanUtil;
import com.example.monitorserver.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

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
    @Around("execution(* com.example.monitorserver.controller.ApiErrorController.*(..)) ||"+
            "execution(* com.example.monitorserver.controller.ErrorController.getFp(..)) ||"+
            "execution(* com.example.monitorserver.controller.ErrorController.whole(..))")
    public Result jurisdiction(ProceedingJoinPoint pjp) throws Throwable {

        String token = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + token);
        User user = (User) MapBeanUtil.map2Object(entries, User.class);
        int position = user.getPosition();
        //TOOD 1.为管理员，可直接通过
        if (position==9){
            return (Result) pjp.proceed();
        }


        else{
            Object[] args1 = pjp.getArgs();
            ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
            MethodSignature signature1 = (MethodSignature) pjp.getSignature();
            Method method1 = signature1.getMethod();
            String[] parameterNames = pnd.getParameterNames(method1);
            Map<String, Object> paramMap = new HashMap<>(32);

            HttpSecretCode httpSecretCode = new HttpSecretCode();
            String aesKey = Constants.AES_KEY;

            for (int i = 0; i < parameterNames.length; i++) {
                paramMap.put(parameterNames[i], args1[i]);
            }
            for (String s : paramMap.keySet()) {
                httpSecretCode = (HttpSecretCode) paramMap.get(s);
            }

            //开始解密
            aesKey = getAesKey(httpSecretCode);

            String json = check(httpSecretCode, aesKey);

            Data data = JSON.parseObject(json, Data.class);
            String projectName = data.getProjectName();


            log.debug("监控权限AOP");
            String userId = user.getUserId();
            //TODO 3.通过项目名获得项目id
            Map<String,Object> condition = new HashMap<>();
            condition.put("project_name",projectName);
            Result byCondition = projectService.getByCondition(condition);
            List<Project> data1 = (List<Project>) byCondition.getData();
            String projectId = data1.iterator().next().getProjectId();
            //TODO 4.查看是否具有权限
            Map<String,Object> map = new HashMap<>();
            map.put("user_id",userId);
            map.put("project_id",projectId);
            Result select = userProjectService.select(map);
            if (select.getData() != null){
                return (Result) pjp.proceed();
            }
        }
        request.getSession();
        Result result = new Result(ResultEnum.USER_EXPIRE);
        response.setHeader("content-type", "text/html;charset=utf-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
        return new Result(ResultEnum.REQUEST_FALSE);
    }

    public String getAesKey(HttpSecretCode httpSecretCode) throws Exception {
        //有一个东西为空,表示你传进来的数据是没用的
        if (ObjectUtils.isEmpty(httpSecretCode) || StringUtils.isBlank(httpSecretCode.getEncryptStr()) || StringUtils.isBlank(httpSecretCode.getEncryptKey())) {
            throw new SystemException(ResultEnum.HTTP_EX_1);
        }

        String key = httpSecretCode.getEncryptKey();

        String aesKey;
        //用私钥对aes密钥进行解密
        try {
            aesKey = RSAUtil.decrypt(key, Constants.PRIVATE_KEY);
        } catch (Exception e) {
            //表示解密失败
            throw new SystemException(ResultEnum.HTTP_EX_1);
        }
        log.debug(aesKey);
        return aesKey;
    }


    public String check(HttpSecretCode httpSecretCode, String aesKey) throws Exception {
        if (StringUtils.isBlank(aesKey)) {
            throw new SystemException(ResultEnum.HTTP_EX_1);
        }

        //有一个东西为空,表示你传进来的数据是没用的
        if (ObjectUtils.isEmpty(httpSecretCode) || StringUtils.isBlank(httpSecretCode.getEncryptStr()) || StringUtils.isBlank(httpSecretCode.getEncryptKey())) {
            throw new SystemException(ResultEnum.HTTP_EX_1);
        }
        String json;
        try {
            json = AesUtil.decrypt(httpSecretCode.getEncryptStr(), aesKey);
        } catch (Exception e) {
            throw new SystemException(ResultEnum.HTTP_EX_1);
        }
        log.debug(json);
        return json;
    }
}


