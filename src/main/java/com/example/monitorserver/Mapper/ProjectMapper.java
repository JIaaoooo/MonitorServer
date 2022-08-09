package com.example.monitorserver.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.Project;
import org.apache.ibatis.annotations.Mapper;


/**
 * @program: monitor server
 * @description: 操作Project表
 * @author: Jiao
 * @create: 2022-08-09 11：09
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
