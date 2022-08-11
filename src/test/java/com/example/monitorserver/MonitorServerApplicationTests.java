package com.example.monitorserver;

import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.controller.ProjectController;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.LogService;
import com.example.monitorserver.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.List;

@SpringBootTest
class MonitorServerApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectController projectController;

    @Test
    void userTest() {
//        DynamicTableNameConfig.setDynamicTableName("user");
        List<User> users = userMapper.selectList(null);
        Iterator<User> iterator = users.iterator();
        while(iterator.hasNext()){
            User user = iterator.next();
            String userId = user.getUserId();
            System.out.println(userId);
        }
    }

    @Test
    void projectTest(){
        /*Project project = new Project();
        project.setProjectName("Jiao");
        project.setProjectDesc("Jiao");
        project.setProjectUrl("Jiao");
        Result result = projectController.saveProject(project);*/
        Result result = projectService.getPageProject(1, 5, 9);
        System.out.println(result.toString());
    }

    @Autowired
    private LogService logService;
    @Test
    void LogTest(){
        logService.createTable();

        //logService.select(null);
    }
}
