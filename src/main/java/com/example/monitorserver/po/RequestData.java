package com.example.monitorserver.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: monitor server
 * @description: 封装请求
 * @author: Jiao
 * @create: 2022-08-10 19：10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestData {

    /** 项目名称 **/
    private String project_name;

    /** 日期时间 **/
    private Date date;

    /** 申请表id **/
    private String applicationId;
}
