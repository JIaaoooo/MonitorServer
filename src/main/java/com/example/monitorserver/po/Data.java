package com.example.monitorserver.po;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @program: monitor server
 * @description: 封装请求数据
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Data {

    private String projectName;

    private String userName;

    private String url;



    /** 时间戳 日期格式 **/
    private Timestamp date;

    private String type;

    /** 查看性能监控的类型 **/
    private String responseType;

    private int option;

}
