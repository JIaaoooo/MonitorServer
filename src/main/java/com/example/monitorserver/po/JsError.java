package com.example.monitorserver.po;


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
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: js错误
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("t_jsError")
public class JsError extends HttpSecretCode {

    @TableId
    private Long id;

    /** 项目名 **/
    private String projectName;

    /** url **/
    private String url;

    /** 报错日期 **/
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    /** 报错数 **/
    @TableField(exist = false)
    private Long count;

}
