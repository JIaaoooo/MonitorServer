package com.example.monitorserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.monitorserver.po.Statistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @program: monitor server
 * @description: 操作Statistics表
 * @author: Jiao
 * @create: 2022-08-11  19：16
 */
@Mapper
public interface StatisticsMapper extends BaseMapper<Statistics> {

    /**
     * 创建小时统计表
     * @param table 表名
     */
    @Update("CREATE TABLE IF NOT EXISTS ${table}(  \n" +
            "  `id` BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "  `project_id` VARCHAR(100) NOT NULL,\n" +
            "  `project_name` VARCHAR(100) NOT NULL,\n" +
            "  `package_name` VARCHAR(50),\n" +
            "  `method` VARCHAR(20),\n" +
            "  `views` BIGINT NOT NULL,\n" +
            "  `visits` BIGINT NOT NULL,\n" +
            "  `defeat` BIGINT ,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ");")
    void createTable(@Param("table")String table);




}
