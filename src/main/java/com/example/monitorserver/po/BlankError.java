package com.example.monitorserver.po;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: monitor server
 * @description: 白屏错误
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlankError extends HttpSecretCode {

    @TableId
    private Long id;

    /** 项目名 **/
    private String projectName;


    /** 报错日期 **/
    private LocalDateTime date;
}
