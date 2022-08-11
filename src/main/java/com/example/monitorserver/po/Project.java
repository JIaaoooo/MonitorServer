package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @program: monitor server
 * @description: 项目实体类
 * @author: Jiao
 * @create: 2022-08-09 15:18
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @TableId
    private long id;

    /** 项目唯一id **/
    private String projectId;

    /** 项目名 **/
    private String projectName;

    /** 项目描述 **/
    private String projectDesc;

    /** 发布地址 **/
    private String projectUrl;



    /** 审核状态 0为审核 1正常 -1审核不通过，不给予展示**/
    private int status;

    /** 解封日期 **/
    private LocalDateTime unsealDate;

}
