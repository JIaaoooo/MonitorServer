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
    LOGIN_SUCCESS(200,"登陆成功"),

    REQUEST_FALSE(400,"请求失败"),
    PARAMETER_NOT_VALID(400,"参数不合法"),



    USER_EXPIRE(400,"用户登录过期"),
    LOGIN_INFORMATION_FALSE(400,"登录信息错误"),
    LOGIN_USER_FROZEN(400,"用户被冻结"),
    REGISTER_NAME_DOUBLE(400,"重复"),
    REGISTER_PHONE_DOUBLE(400,"电话号码重复"),
    REGISTER_EMAIL_DOUBLE(400,"邮箱重复"),
    SERVER_INTERNAL_ERROR(500,"服务器正在忙碌中,请稍后试试吧"),

    REGISTER_SUCCESS(200,"注册成功"),

    SELECT_BLANK(200,"查询为空"),
    LOGOUT_SUCCESS(200,"退出成功"),

    SELECT_PAGE(200,"分页查询"),


    UPDATE_SUCCESS(200,"更新修改成功"),

    SELECT_LIKE(200,"条件查询"),

    INSERT_SUCCESS(200,"存入成功"),
    SELECT_SUCCESS(200,"查询成功"),

    APPLICATION_MESSAGE(200,"申请表信息"),

    FREEZE_SUCCESS(200,"冻结成功"),
    DELETE_SUCCESS(200,"删除成功"),

    CREATE_SUCCESS(200,"创建成功"),
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
