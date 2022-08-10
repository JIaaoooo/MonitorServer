package com.example.monitorserver.aop;//package cn.stopyc.aop;
//
//import cn.stopyc.constant.Constants;
//import cn.stopyc.constant.ExceptionEnum;
//import cn.stopyc.constant.Result;
//import cn.stopyc.exception.SystemException;
//import cn.stopyc.po.HttpSecretCode;
//import cn.stopyc.util.AesUtil;
//import cn.stopyc.util.RSAUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.commons.lang.StringEscapeUtils;
//import org.apache.commons.lang.StringUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.owasp.esapi.ESAPI;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.DefaultParameterNameDiscoverer;
//import org.springframework.core.ParameterNameDiscoverer;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @program: aop_annotation
// * @description:
// * @author: stop.yc
// * @create: 2022-08-08 17:58
// **/
//@Aspect
//@Component
//public class SecretAop {

//TODO 这个是http加密,因为我们开发用postman无法进行自动加密,所以先注释掉,然后切入的路径记得改
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(SecretAop.class);
//
//    //定义切点,这是解密的方法
//    @Pointcut("@within(cn.stopyc.annotation.Secret) || @annotation(cn.stopyc.annotation.Secret)")
//    public void pointcut() {
//    }
//
//    // 环绕切面
//    @Around("pointcut()")
//    public Result around(ProceedingJoinPoint pjp) throws Throwable {
//
//
//        // 参数值
//        Object[] args1 = pjp.getArgs();
//        ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
//        MethodSignature signature1 = (MethodSignature) pjp.getSignature();
//        Method method1 = signature1.getMethod();
//        String[] parameterNames = pnd.getParameterNames(method1);
//        Map<String, Object> paramMap = new HashMap<>(32);
//        for (int i = 0; i < parameterNames.length; i++) {
//            paramMap.put(parameterNames[i], args1[i]);
//        }
//
//
//
//
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        assert attributes != null;
//
//        //request对象
//        HttpServletRequest request = attributes.getRequest();
//
//        //http请求方法  post get
//        String httpMethod = request.getMethod().toLowerCase();
//        System.out.println("httpMethod = " + httpMethod);
//
//        //方法的形参参数
//        Object[] args = pjp.getArgs();
//        // 获取被代理对象
//        Object target = pjp.getTarget();
//
//        // 获取通知签名
//        MethodSignature signature = (MethodSignature) pjp.getSignature();
//
//        //method方法
//        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
//
//        HttpSecretCode httpSecretCode = new HttpSecretCode();
//
//        String aesKey = Constants.AES_KEY;
//
//        //如果是get方法,直接放行
//        if (HttpMethod.GET.toString().equalsIgnoreCase(httpMethod) || HttpMethod.DELETE.toString().equalsIgnoreCase(httpMethod)) {
//        } else {
//            //注释掉没用的代码
//            {//            //获取前端传进来的参数
////            InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), "UTF-8");
////            StringBuilder stringBuilder;
////            try (BufferedReader br = new BufferedReader(inputStreamReader)) {
////                String line;
////                stringBuilder = new StringBuilder();
////                while ((line = br.readLine()) != null) {
////                    stringBuilder.append(line);
////                }
////            }
////            String oriData = stringBuilder.toString();
////
////            System.out.println(oriData);
////
////            httpSecretCode = JSON.parseObject(oriData, HttpSecretCode.class);
//
//
////            // 获取被代理方法
////            Method pointMethod = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
////            // 获取被代理方法上面的注解@Secret
////            Secret secret = pointMethod.getAnnotation(Secret.class);
////
////            Class clazz = secret.value();
////
////            Object cast = clazz.cast(args[0]);      //将args[i]转换为clazz表示的类对象
////            // 通过反射，执行getEncryptStr()方法，获取加密数据
////            Method method1 = clazz.getMethod(getMethedName("encryptStr"));
////            Method method2 = clazz.getMethod(getMethedName("encryptKey"));
////            // 执行方法，获取加密数据
////            String encryptStr = (String) method1.invoke(cast);
////            String encryptKey = (String) method2.invoke(cast);
////
////            httpSecretCode.setEncryptStr(encryptStr);
////            httpSecretCode.setEncryptKey(encryptKey);
//            }
//
//
//            for (String s : paramMap.keySet()) {
//                System.out.println("paramMaps = " + paramMap.get(s));
//                httpSecretCode = (HttpSecretCode) paramMap.get(s);
//            }
//
//            //开始解密
//            aesKey = getAesKey(httpSecretCode);
//
//            System.out.println("aesKey = " + aesKey);
//
//            String json = check(httpSecretCode, aesKey);
//            //在这里进行转义
//
//            System.out.println("转义之前的json = " + json);
//
//
//
//
//
//            System.out.println("重新序列化json = " + json);
//            if (args.length > 0) {
//                args[0] = JSONObject.parseObject(json, args[0].getClass());
//            }
//        }
//
//        System.out.println(httpSecretCode);
//
//
//        Object o = pjp.proceed(args);
//
//        Result result = (Result) o;
//
//        if (null == result.getData()) {
//            return result;
//        }
//
//        //data不为空的话,需要加密
//        System.out.println("未加密result = " + result);
//
//        try {
//            result.setData(AesUtil.encrypt(JSON.toJSONString(result.getData()), aesKey));
//        } catch (Exception e) {
//            throw new SystemException(ExceptionEnum.HTTP_EX_0);
//        }
//
//        System.out.println("加密后的result = " + result);
//        return result;
//    }
//
//
//    public String getAesKey(HttpSecretCode httpSecretCode) throws Exception {
//        //有一个东西为空,表示你传进来的数据是没用的
//        if (ObjectUtils.isEmpty(httpSecretCode) || StringUtils.isBlank(httpSecretCode.getEncryptStr()) || StringUtils.isBlank(httpSecretCode.getEncryptKey())) {
//            throw new SystemException(ExceptionEnum.HTTP_EX_1);
//        }
//
//        String key = httpSecretCode.getEncryptKey();
//
//        String aesKey;
//        //用私钥对aes密钥进行解密
//        try {
//            aesKey = RSAUtil.decrypt(key, Constants.PRIVATE_KEY);
//        } catch (Exception e) {
//            //表示解密失败
//            throw new SystemException(ExceptionEnum.HTTP_EX_1);
//        }
//
//        return aesKey;
//    }
//
//
//    public String check(HttpSecretCode httpSecretCode, String aesKey) throws Exception {
//        if (StringUtils.isBlank(aesKey)) {
//            throw new SystemException(ExceptionEnum.HTTP_EX_1);
//        }
//
//        //有一个东西为空,表示你传进来的数据是没用的
//        System.out.println(httpSecretCode);
//        if (ObjectUtils.isEmpty(httpSecretCode) || StringUtils.isBlank(httpSecretCode.getEncryptStr()) || StringUtils.isBlank(httpSecretCode.getEncryptKey())) {
//            throw new SystemException(ExceptionEnum.HTTP_EX_1);
//        }
//        String json;
//        try {
//            json = AesUtil.decrypt(httpSecretCode.getEncryptStr(), aesKey);
//        } catch (Exception e) {
//            throw new SystemException(ExceptionEnum.HTTP_EX_1);
//        }
//
//        return json;
//    }
//}
