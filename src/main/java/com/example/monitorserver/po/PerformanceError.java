package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: 性能监控实体类
 * @author: Jiao
 * @create: 2022-08-14 9：39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceError extends HttpSecretCode  {

    @TableId
    private Long id;

    /** 项目名  **/
    private String projectName;

    /** 存储日期 **/
    private LocalDateTime date;

    /** DOM Ready 加载用时 **/
    private Long domContentLoadedTime;

    /** dns加载用时 **/
    private Long dns;

    /** 完整加载用时 **/
    private Long localTime;

    /** 首次可交互时间 **/
    private Long timeToInteractive;

    /** 首次渲染耗时——FP **/
    private Long firstPaint;

    /** 最重要内容的渲染耗时——LCP **/
    private Long largestContentfulPaint;

    /**首次有意义的渲染耗时——FMP  **/
    private Long firstMeaningfulPaint;

    /** 首次有内容渲染耗时——LCP **/
    private Long firstContentfulPaint;

    /** 用户首次交互时间 **/
    private Long fid;

    /** 卡顿时间 **/
    private Long longTask ;

    /** 平均耗时 **/
    @TableField(exist = false)
    private Long ConsumeTime;

}
