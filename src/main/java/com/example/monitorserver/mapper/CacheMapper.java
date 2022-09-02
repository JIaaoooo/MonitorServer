package com.example.monitorserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.Cache;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: monitor server
 * @description: cacheMapper
 * @author: Jiao
 * @create: 2022-09-02
 */
@Mapper
public interface CacheMapper extends BaseMapper<Cache> {
}
