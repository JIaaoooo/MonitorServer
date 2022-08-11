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
public class Application {
    @TableId
    private long id;

    /** 申请表唯一ID **/
    private String applicationId;

    /** 申请人ID **/
    private String applicantId;

    /** 操作项目的ID **/
    private String  projectId;

    /** 操作类型  0-发布项目(管理员)，1-请求监控(发布者)，2-邀请发布者(普通用户)，3-删除项目(所有发布者) **/
    private int type;


    /** 当status为 0 的时候该申请通过 **/
    private int status;

}
