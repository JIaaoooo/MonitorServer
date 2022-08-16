package com.example.monitorserver.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString(callSuper = true)
@Accessors(chain = true)
public class ResourceError extends HttpSecretCode{
    @TableId
    private Long id;

    /** 项目名 **/
    private String projectName;


    /** 报错日期 **/
    private LocalDateTime date;

    /** 资源路径 **/
    private String filename;

    /**  错误名称  **/
    private String tagname;
}
