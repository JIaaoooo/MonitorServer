package com.example.monitorserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.monitorserver.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: monitor server
 * @description: 操作User表
 * @author: Jiao
 * @create: 2022-08-08 12：23
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Page<User> queryUsers(Page<User> page);
}
