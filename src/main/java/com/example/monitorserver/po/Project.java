package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
@TableName("t_project")
public class Project extends HttpSecretCode{


    /** 项目唯一id **/
    private String projectId;

    /** 发布人ID **/
    @TableField(exist = false)
    private String userId;

    private String username;

    /** 项目名 **/
    private String projectName;


    /** 项目描述 **/
    private String projectDesc;

    /** 发布地址 **/
    private String projectUrl;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime registerDate;


    /** 审核状态 0为审核 1正常 2审核不通过，-1 项目冻结 不给予展示**/
    private int status;

    /** 获取前端status类型  **/
    @TableField(exist = false)
    private String pass;

    @TableField(exist = false)
    private Timestamp time;

    /** 解封日期 **/
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.VARCHAR)  //可以修改为null值
    private LocalDateTime unsealDate;

    /** 在申请返回时使用，告知前端该项目下的申请是什么类型  **/
    @TableField(exist = false)
    private int appliType;

    @TableField(exist = false)
    private String appliId;

    @TableField(exist = false)
    private String appiUser;

    @TableField(exist = false)
    private Long PV;

    @TableField(exist = false)
    private Long UV;

    @TableField(exist = false)
    private double rate;
}
