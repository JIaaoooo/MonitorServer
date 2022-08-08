package com.example.monitorserver.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monitor server
 * @description: 返回值包装
 * @author: Jiao
 * @create: 2022-08-08 09:46
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**编号*/
    private Integer code;
    /**信息*/
    private String msg;

    /**数据*/
    private T data;
}
