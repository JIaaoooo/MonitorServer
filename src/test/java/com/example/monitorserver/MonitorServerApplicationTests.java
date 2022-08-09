package com.example.monitorserver;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.monitorserver.Mapper.UserMapper;
import com.example.monitorserver.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MonitorServerApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {

       /* Page<T> page = new Page<>(1,3);
        page = userMapper.selectPage(page, null);
        List<T> records = page.getRecords();
        System.out.println(records);
        for (T record : records) {
            System.out.println(record);
        }*/
    }

}
