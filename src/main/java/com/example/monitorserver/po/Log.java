package com.example.monitorserver.po;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: 监控日志
 * @author: Jiao
 * @create: 2022-08-10 19：10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
public class Log extends HttpSecretCode{


    /** 访问者的ip  **/
    private String ip;

    private String uri;

    /**  项目的url **/
    private String projectUrl;

    /** 调用方法  **/
    private String method;

    /** 调用的包  **/
    private String packageName;

    /** 入参  **/
    private String inParameters;

    /** 出参  **/
    private String outParameters;

    /**  异常、报错的堆栈信息 **/
    private String exception;

    /** 访问特征 **/
    private String traits;

    /** 访问日期 **/
    private LocalDateTime visitDate;

    /** 响应时间 **/
    private int responseTime;



}
