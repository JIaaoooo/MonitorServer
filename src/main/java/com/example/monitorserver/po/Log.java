package com.example.monitorserver.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Log {
    @TableId
    private long id;


    /** 访问者的ip  **/
    private String ip;

    /**  项目的id **/
    private String project_id;

    /** 调用方法  **/
    private String interfaceName;

    /** 入参  **/
    private String inArgs;

    /** 出参  **/
    private String outArgs;

    /**  异常、报错的堆栈信息 **/
    private String exception;

    /** 访问特征 **/
    private String trait;

    /** 访问日期 **/
    private LocalDateTime visitDate;

    /** 响应时间 **/
    private String responseTime;


}
