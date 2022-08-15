package com.example.monitorserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.PerformanceError;
import org.apache.ibatis.annotations.Mapper;
/**
 * @program: monitor server
 * @description: 资源检测Mapper
 * @author: Jiao
 * @create: 2022-08-14 13：56
 */
@Mapper
public interface PerformanceErrorMapper extends BaseMapper<PerformanceError> {
}
