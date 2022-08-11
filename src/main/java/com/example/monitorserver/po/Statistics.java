package com.example.monitorserver.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: monitor server
 * @description: 统计包装
 * @author: Jiao
 * @create: 2022-08-08 09:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Statistics {

    @TableId
    private Long id;

    private String project_id;

    /** 访问量 **/
    private Long page_view;

    /** 访问人次 **/
    private Long visits;

    /** 方法名 **/
    private String method;

    /** 包名 **/
    private String packageName;
}
