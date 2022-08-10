package com.example.monitorserver.exception;


import com.example.monitorserver.constant.StatusCode;

/**
 * @program: aop_annotation
 * @description: 系统异常
 * @author: stop.yc
 * @create: 2022-08-09 22:14
 **/
public class SystemException extends RuntimeException{

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public SystemException(StatusCode statusCode) {
        super(statusCode.getMsg());
        this.code = statusCode.getCode();
    }

    public SystemException(StatusCode statusCode, Throwable cause) {
        super(statusCode.getMsg(), cause);
        this.code = statusCode.getCode();
    }
}
