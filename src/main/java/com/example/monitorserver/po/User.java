package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.*;
import com.example.monitorserver.constant.Constants;
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

import java.time.LocalDateTime;
import javax.validation.constraints.Pattern;

/**
 * @program: monitor server
 * @description: 用户对象
 * @author: Jiao
 * @create: 2022-08-08 09:46
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_user")
//TODO 加密有问题,继承的父类属性添加到了查询里
public class User extends HttpSecretCode{


    /**  用户唯一id **/
    //TODO 分组校验还没加入,等等
    private String userId;

    /** 用户名 **/
    @Pattern(regexp = Constants.REGEX_NAME,message = "用户名长度在4到20位")
    private String username;



    /** 密码 **/
    @TableField(select = false)
    @Pattern(regexp = Constants.REGEX_PWD,message = "密码长度在6到20位,且至少包括一位数字和大小写字母")
    private String password;

    /** 绑定的电话 **/
//    @Pattern(regexp = Constants.REGEX_PHONE,message = "手机格式错误")
    private String phone;


    /**用户状态 0->正常用户 -1被冻结用户**/
    private int position;

    /** 注册时间 **/
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime registerDate;

    /** 解封日期 **/
    @TableField(updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.VARCHAR)
    private LocalDateTime unsealDate;


    /** 该用户是否有信息 **/
    private int message;

    /**  用户是否在线  **/
    @TableField(exist = false)
    private int onLive;
}
