
package com.example.monitorserver.aop;//package cn.stopyc.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.monitorserver.constant.Constants;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.HttpSecretCode;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.utils.AesUtil;
import com.example.monitorserver.utils.RSAUtil;
import com.example.monitorserver.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
@Slf4j
public class SecretAop {


    private static final Logger LOGGER = LoggerFactory.getLogger(SecretAop.class);

    //定义切点,这是解密的方法
    @Pointcut("@within(com.example.monitorserver.annotation.Secret) || @annotation(com.example.monitorserver.annotation.Secret)")
    public void pointcut() {
    }

    // 环绕切面
    @Around("pointcut()")
    public Result around(ProceedingJoinPoint pjp) throws Throwable {


        // 参数值
        Object[] args1 = pjp.getArgs();
        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        MethodSignature signature1 = (MethodSignature) pjp.getSignature();
        Method method1 = signature1.getMethod();
        String[] parameterNames = pnd.getParameterNames(method1);
        Map<String, Object> paramMap = new HashMap<>(32);
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args1[i]);
        }




        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        assert attributes != null;

        //request对象
        HttpServletRequest request = attributes.getRequest();

        //http请求方法  post get
        String httpMethod = request.getMethod().toLowerCase();

        //方法的形参参数
        Object[] args = pjp.getArgs();
        // 获取被代理对象
        Object target = pjp.getTarget();

        // 获取通知签名
        MethodSignature signature = (MethodSignature) pjp.getSignature();

        //method方法
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        HttpSecretCode httpSecretCode = new HttpSecretCode();

        String aesKey = Constants.AES_KEY;

        //如果是get方法,直接放行
        if (HttpMethod.GET.toString().equalsIgnoreCase(httpMethod) || HttpMethod.DELETE.toString().equalsIgnoreCase(httpMethod)) {
        } else {

            for (String s : paramMap.keySet()) {
                httpSecretCode = (HttpSecretCode) paramMap.get(s);
            }

            //开始解密
            aesKey = getAesKey(httpSecretCode);

            String json = check(httpSecretCode, aesKey);
            //在这里进行转义

            if (args.length > 0) {
                args[0] = JSONObject.parseObject(json, args[0].getClass());
            }
        }

        log.debug(httpSecretCode.toString());


        Object o = pjp.proceed(args);

        Result result = (Result) o;

        if (null == result.getData()) {
            return result;
        }

        //data不为空的话,需要加密
        System.out.println("未加密result = " + result);

        try {
            result.setData(AesUtil.encrypt(JSON.toJSONString(result.getData()), aesKey));
        } catch (Exception e) {
            throw new SystemException(ResultEnum.HTTP_EX_0);
        }

//        System.out.println("加密后的result = " + result);
        return result;
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

