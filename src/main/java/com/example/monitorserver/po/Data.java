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

    private String date;

    private String type;

    private int option;

}
