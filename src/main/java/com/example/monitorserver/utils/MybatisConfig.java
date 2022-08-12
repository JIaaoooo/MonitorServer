package com.example.monitorserver.utils;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @program: monitor server
 * @description: 动态表明
 * @author: Jiao
 * @create: 2022-08-10 22:30
 */
@Configuration

public class MybatisConfig {

    private final static String common = "t_";
    private static ThreadLocal<String> table = new ThreadLocal<>();

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        HashMap<String, TableNameHandler> map = new HashMap<String, TableNameHandler>(2) {{
            put(common, (sql, tableName) -> {
                return table.get();
            });
        }};
        dynamicTableNameInnerInterceptor.setTableNameHandler(map.get(common));
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }

    //设置表名
    public static void setDynamicTableName(String tableName){
        table.set(tableName);
    }


}
