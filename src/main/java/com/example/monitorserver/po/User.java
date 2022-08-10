package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: monitor server
 * @description: 用户对象
 * @author: Jiao
 * @create: 2022-08-08 09:46
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_user")
public class User {

    /**  用户唯一id **/
    private String userId;

    /** 用户名 **/
    private String username;

    /** 密码 **/
    @TableField(select = false)
    private String password;

    /** 绑定的电话 **/
    private String phone;


    /**用户状态 0->正常用户 -1被冻结用户**/
    private int position;

    /** 注册时间 **/
    private Date registerDate;

    /** 解封日期 **/
    private Date unsealDate;


    /** 该用户是否有信息 **/
    private int message;
}
