package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: redis缓存的数据格式
 * @author: Jiao
 * @create: 2022-09-03
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cache {

    private String projectName;

    /** 错误类型 **/
    private String type;

    /** 该类型的错误数 **/
    private Long count;

    /** 该错误占总错误百分比 **/
    private Long percent;

    /** 入库时间 **/
    private LocalDateTime date;

    /** 详细分支 **/
    private String branch;

    /** 该时段的总错误数 **/
    @TableField(exist = false)
    private Long total;

    /** 上一时段的总错误数 **/
    @TableField(exist = false)
    private Long LastCount;

}
