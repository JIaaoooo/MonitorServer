package com.example.monitorserver.po;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monitor server
 * @description: 用户所对应项目的权限
 * @author: Jiao
 * @create: 2022-08-09 18:33
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_project_user")
public class UserProject {

    @TableId
    private Long id;

    private Long userId;

    private Long projectId;

    /**  用户对应这个项目的权限  1是发布者  2是监控者  **/
    private int type;
}
