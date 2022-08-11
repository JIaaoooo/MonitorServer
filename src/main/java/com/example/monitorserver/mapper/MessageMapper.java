package com.example.monitorserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: monitor server
 * @description: 操作Message表
 * @author: Jiao
 * @create: 2022-08-09 17:53
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
