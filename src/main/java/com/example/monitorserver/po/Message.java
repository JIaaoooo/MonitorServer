package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @program: monitor server
 * @description: 项目实体类
 * @author: Jiao
 * @create: 2022-08-09 15:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_message")
public class Message extends HttpSecretCode {
    @TableId
    private long id;



    /** 接收方Id **/
    private String applicationId;

    /** 用户Id **/
    private String userId;

    /** 该申请信息是否已处理 **/
    private int handle;
}
