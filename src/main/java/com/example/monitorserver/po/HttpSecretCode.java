package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: aop_annotation
 * @description: http通信加密Vo
 * @author: stop.yc
 * @create: 2022-08-08 21:26
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HttpSecretCode {

    @TableField(select = false)
    private String encryptStr;

    @TableField(select = false)
    private String encryptKey;
}
