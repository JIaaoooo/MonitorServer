package com.example.monitorserver.constant;
/**
 * @program: monitor server
 * @description: redis默认值
 * @author: Jiao
 * @create: 2022-08-08 10：39
 */
public enum RedisEnum {

    //登录成功redis缓存key的前缀名,登录成功
    LOGIN_TOKEN(200,"login:token:"),

    //登录成功TOKEN缓存时间，超过缓存时间默认退出登录，登录闲置时间设置为1小时
    TOKEN_EXITS(1,"过期时间"),

    INDEX_KEY(2,"index"),
    ;



    /**
     * 编号
     */
    private Integer code;
    /**
     * 信息
     */
    private String msg;

    RedisEnum(Integer code, String msg) {
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
