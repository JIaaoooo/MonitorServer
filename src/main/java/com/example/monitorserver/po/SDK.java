package com.example.monitorserver.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * @program: monitor server
 * @description: 前端发送信息封装类
 * @author: Jiao
 * @create: 2022-08-15 17：21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class SDK {

    private String type;

    private Object data;
}
