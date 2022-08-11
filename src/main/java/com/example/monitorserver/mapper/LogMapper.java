package com.example.monitorserver.mapper;

import com.example.monitorserver.po.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface LogMapper {

    /**
     * 存储日志
     * @param log 日志信息
     */
    void add(Log log);

    /**
     * 创建存储日志表 表明格式t_visit_日期   每天创建一个新表
     * @param table 表名
     */
    void createTable(@Param("table")String table);

    /**
     * 根据项目Id获取监控数据
     * @param map
     * @return
     */
    Log select(Map<String,Object> map);


}
