package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monitor server
 * @description: 白屏错误
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class apiError {

    @TableId
    private Long id;

    /** 项目名称 **/
    private String projectName;

    /**  失败次数 **/
    private Long defeatCount;

    /** 总访问量**/
    private Long visits;

    /** 平均延迟时间 **/
    private Long responseTime;

    /** 错误率 **/
    @TableField(exist = false)
    private double defeat;
}
