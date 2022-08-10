package com.example.monitorserver;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.monitorserver.Mapper.UserMapper;
import com.example.monitorserver.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.List;

@SpringBootTest
class MonitorServerApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        List<User> users = userMapper.selectList(null);
        Iterator<User> iterator = users.iterator();
        while(iterator.hasNext()){
            User user = iterator.next();
            String userId = user.getUserId();
            System.out.println(userId);
        }

    }

}
