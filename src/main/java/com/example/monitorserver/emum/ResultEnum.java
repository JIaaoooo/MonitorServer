package com.example.monitorserver.emum;

/**
 * @program: monitor server
 * @description: 结果返回默认值
 * @author: Jiao
 * @create: 2022-08-08 10：39
 */
public enum ResultEnum {

    /**
     * 登陆成功
     */
    LOGIN_SUCCESS(200,"登陆成功"),
    LOGIN_INFORMATION_FALSE(4011,"登录信息错误"),
    LOGIN_USER_FROZEN(4012,"用户被冻结"),
    REGISTER_NAME_DOUBLE(4001,"用户名重复"),
    REGISTER_PHONE_DOUBLE(4002,"电话号码重复"),
    REGISTER_EMAIL_DOUBLE(4003,"邮箱重复"),

    REGISTER_SUCCESS(500,"注册成功"),

    LOGOUT_SUCCESS(2001,"退出成功"),

    SELECT_PAGE(2001,"分页查询")
    ;
    /**
     * 编号
     */
    private Integer code;
    /**
     * 信息
     */
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
