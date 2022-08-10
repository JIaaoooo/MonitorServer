package com.example.monitorserver.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: monitor server
 * @description: 结果返回默认值
 * @author: Jiao
 * @create: 2022-08-08 10：39
 */
@Getter
@AllArgsConstructor
public enum ResultEnum implements StatusCode{

    /**
     * 登陆成功
     */
    LOGIN_SUCCESS(201,"登陆成功"),

    REQUEST_FALSE(400,"请求失败"),
    PARAMETER_NOT_VALID(400,"参数不合法"),

    LOGIN_INFORMATION_FALSE(4011,"登录信息错误"),
    LOGIN_USER_FROZEN(4012,"用户被冻结"),
    REGISTER_NAME_DOUBLE(4001,"用户名重复"),
    REGISTER_PHONE_DOUBLE(4002,"电话号码重复"),
    REGISTER_EMAIL_DOUBLE(4003,"邮箱重复"),
    SERVER_INTERNAL_ERROR(500,"服务器正在忙碌中,请稍后试试吧"),

    REGISTER_SUCCESS(500,"注册成功"),

    LOGOUT_SUCCESS(2001,"退出成功"),

    SELECT_PAGE(2001,"分页查询"),


    UPDATE_SUCCESS(2002,"更新修改成功"),

    SELECT_LIKE(2003,"条件查询"),

    INSERT_SUCCESS(2004,"存入成功"),
    SELECT_SUCCESS(2006,"查询成功"),

    APPLICATION_MESSAGE(2007,"申请表信息"),

    FREEZE_SUCCESS(2008,"冻结成功"),
    DELETE_SUCCESS(2009,"删除成功"),

    REQUEST_SUCCESS(200,"请求成功"),


    //异常

    //系统异常
    // 通信异常,0为加密,1为解密,因为不能把详细信息进行暴露
    HTTP_EX_0(10000,"Http通信异常"),
    HTTP_EX_1(10010,"Http通信异常"),

    ;
    /**
     * 编号
     */
    private int code;
    /**
     * 信息
     */
    private String msg;
}
