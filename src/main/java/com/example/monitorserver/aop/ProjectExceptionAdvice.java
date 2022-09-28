package com.example.monitorserver.aop;

import com.alibaba.fastjson.JSONException;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.exception.BusinessException;
import com.example.monitorserver.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: Software-management-platform
 * @description: 异常处理器
 * @author: stop.yc
 * @create: 2022-07-24 19:10
 **/

@RestControllerAdvice
@Slf4j
@Order(1)
public class ProjectExceptionAdvice {

/**
      日志打印
**/

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectExceptionAdvice.class);

/**处理自定义异常SystemException **/

    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        System.out.println("系统异常了"+ex.getMessage());
        LOGGER.error("发生了异常:{}",(Throwable) ex);
        return new Result(ex.getCode(),ex.getMessage(),null);
    }
//处理自定义异常BusinessException

    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException ex){
        System.out.println("业务异常了"+ex.getMessage());
        LOGGER.error("发生了异常:{}",(Throwable) ex);
        return new Result(ex.getCode(),ex.getMessage(),null);
    }

//处理数据参数异常

    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class,HttpMessageNotReadableException.class})
    public Result doMethodArgumentNotValidException(Exception  e){

        LOGGER.error("发生了异常:{}",(Throwable) e);

        if (e instanceof MethodArgumentNotValidException) {

            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            //获取所有的错误集合,获取其提示信息,封装
            return new Result(ResultEnum.PARAMETER_NOT_VALID.getCode(),
                    ex.getBindingResult().getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.joining("; ")));
        } else if (e instanceof ConstraintViolationException) {

            ConstraintViolationException ex = (ConstraintViolationException) e;
            return new Result(ResultEnum.PARAMETER_NOT_VALID.getCode(),
                    ex.getConstraintViolations().stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("; ")));
        } else if (e instanceof BindException) {

            BindException ex = (BindException) e;
            return new Result(ResultEnum.PARAMETER_NOT_VALID.getCode(),
                    ex.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.joining("; ")));
        }else if (e instanceof HttpMessageNotReadableException) {
            return new Result(ResultEnum.PARAMETER_NOT_VALID.getCode(),"没有请求参数");
        }

        return null;
    }

/** 除了自定义的异常处理器，保留对Exception类型的异常处理，用于处理非预期的异常 **/

    @ExceptionHandler(Exception.class)
    public Result doOtherException(Exception ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员

        LOGGER.error("在这里发生了异常:{}",ex);
        System.out.println("错误");
        if (ex instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException e = (HttpMessageNotReadableException)ex;
            return new Result(ResultEnum.PARAMETER_NOT_VALID.getCode(), Objects.requireNonNull(e.getMessage()).substring(e.getMessage().indexOf(":") + 1, e.getMessage().indexOf(";")),null);
        }else if (ex instanceof JSONException){
            return new Result(ResultEnum.PARAMETER_NOT_VALID.getCode(),
                    ex.getMessage());
        }

        return new Result(ResultEnum.SERVER_INTERNAL_ERROR.getCode(),ResultEnum.SERVER_INTERNAL_ERROR.getMsg(),null);
    }
}
