package com.example.monitorserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.ResourceError;
import org.apache.ibatis.annotations.Mapper;
/**
 * @program: monitor server
 * @description: 资源错误Mapper
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */
@Mapper
public interface ResourceErrorMapper extends BaseMapper<ResourceError> {
}
