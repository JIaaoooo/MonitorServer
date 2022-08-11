package com.example.monitorserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.Application;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: monitor server
 * @description: 操作Application表
 * @author: Jiao
 * @create: 2022-08-09 11：09
 */

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
