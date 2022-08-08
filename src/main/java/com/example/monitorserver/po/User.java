package com.example.monitorserver.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monitor server
 * @description: 用户对象
 * @author: Jiao
 * @create: 2022-08-08 09:46
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @TableId(type = IdType.AUTO)
    private int id;

    private String username;

    private String password;

    private String phone;

    private String email;

    /**用户状态 0->正常用户 -1被冻结用户**/
    private int position;

}
