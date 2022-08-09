package com.example.monitorserver.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.UserProject;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: monitor server
 * @description: 操作t_project_user表
 * @author: Jiao
 * @create: 2022-08-09 18:39
 */
@Mapper
public interface UserProjectMapper extends BaseMapper<UserProject> {
}
