package com.example.monitorserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Mapper
public interface LogMapper extends BaseMapper<Log> {


    /**
     * 创建存储日志表 表明格式t_visit_日期   每天创建一个新表
     * @param table 表名
     */
    @Update("CREATE TABLE IF NOT EXISTS ${table} (\n" +
            "`id` BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "`ip` VARCHAR(100) NOT NULL ,\n" +
            "`project_url` VARCHAR(100) NOT NULL,\n" +
            "`package_name` VARCHAR(100) NOT NULL,\n" +
            "`method` VARCHAR(100) NOT NULL,\n" +
            "`in_parameters` VARCHAR(50) NOT NULL,\n" +
            "`out_parameters` VARCHAR(500) NOT NULL,\n" +
            "`exception` VARCHAR(100) ,\n" +
            "`traits` VARCHAR(10) NOT NULL,\n" +
            "`visit_date` DATETIME NOT NULL,\n" +
            "`response_time` INT NOT NULL,\n" +
            "PRIMARY KEY (`id`)\n" +
            ");")
    void createTable(@Param("table")String table);




    /**
     * 创建月统计表
     * @param table 表名
     */
    void createMonthSumTable(@Param("table")String table);

}
