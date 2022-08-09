package com.example.monitorserver.po;

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
@TableName(value="t_application")
public class Application {

    /** 申请列表ID **/
    private long applicationId;

    /** 申请人ID **/
    private long applicantId;


    /** 操作类型  0-发布项目(管理员)，1-请求监控(发布者)，2-邀请发布者(普通用户)，3-删除项目(所有发布者) **/
    private int type;

    /** 操作项目的ID **/
    private long  projectId;

    /** 当status为 0 的时候该申请通过 **/
    private int status;

}
