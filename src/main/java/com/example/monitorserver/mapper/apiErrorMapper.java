package com.example.monitorserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.apiError;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: monitor server
 * @description: api错误Mapper
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */
@Mapper
public interface apiErrorMapper extends BaseMapper<apiError> {
}
