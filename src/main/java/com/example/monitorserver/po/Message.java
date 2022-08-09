package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monitor server
 * @description: 项目实体类
 * @author: Jiao
 * @create: 2022-08-09 15:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_message")
public class Message {

    @TableId
    private long messageId;

    /** 接收方Id **/
    private long applicationId;

    /** 用户Id **/
    private long userId;
}
