package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: api错误
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_apiError")
public class apiError extends HttpSecretCode{

    @TableId
    private Integer id;

    /** 访问者的ip  **/
    private String ip;

    private String uri;

    /**  项目的url **/
    private String projectUrl;

    /** 项目名称 **/
    private String projectName;


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

    /** 延迟时间 **/
    private Long responseTime;

    /** 总访问量**/
    @TableField(exist = false)
    private Long visits;

    /** 总访问人次 **/
    @TableField(exist = false)
    private Long visits_people;

    /**  失败次数 **/
    @TableField(exist = false)
    private Long defeatCount;

    /** 平均延迟时间 **/
    @TableField(exist = false)
    private Double AvgResponseTime;

    /** 错误率 **/
    @TableField(exist = false)
    private Double rate;

    /** 报错数 **/
    @TableField(exist = false)
    private Long count;

    @TableField(exist = false)
    private Double percent;


    @TableField(exist = false)
    private String  dateStr;

    @TableField(exist = false)
    private String type;

    @TableField(exist = false)
    private String dateType;

    @TableField(exist = false)
    private Long PV;

    @TableField(exist = false)
    private Long UV;

    @TableField(exist = false)
    private Long currentPage;

    @TableField(exist = false)
    private Long pageSize;

}
