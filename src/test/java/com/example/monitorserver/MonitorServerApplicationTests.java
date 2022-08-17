package com.example.monitorserver;

import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.controller.ProjectController;
import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

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
        /*List<User> users = userMapper.selectList(null);
        Iterator<User> iterator = users.iterator();
        while(iterator.hasNext()){
            User user = iterator.next();
            String userId = user.getUserId();
            System.out.println(userId);
        }*/
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.LOGIN_TOKEN.getMsg() + "1");
        if(entries.isEmpty()){
            System.out.println("null");
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



    @Test
    void intTest(){
        //TODO 1.获得现在的小时数
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Integer now = Integer.valueOf(sdf.format(calendar.getTime()));
        //TODO 2.从0时开始获取统计数据
        int count = (~(now-1));
        System.out.println(count);
    }


    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Test
    void ManageTest(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("1","1");
        redisTemplate.opsForHash().putAll("王健豪",map);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("王健豪");
        redisTemplate.opsForHash().putAll("王健豪",map);
        Iterator<String> iterator = redisTemplate.keys("王".concat("*")).iterator();
        while (iterator.hasNext()){
            System.out.println("redis中缓存的key"+iterator.next());
        }
        redisTemplate.opsForHash().putAll("王健豪",map);
        Iterator<String> iterator2 = redisTemplate.keys("王".concat("*")).iterator();
        while (iterator2.hasNext()){
            System.out.println("redis中缓存的key"+iterator2.next());
        }
    }

    @Test
    void DateTest() {
        /*LocalDateTime dateTime = LocalDateTime.now();
        long now = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        LocalDateTime time = dateTime.minusDays(1);
        long last = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println(now>last);*/

        /*Iterator<String> iterator = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg().concat("*")).iterator();
        while (iterator.hasNext()){
            redisTemplate.delete(iterator.next());
            }
        }*/

        String number = "1";
        int status = Integer.parseInt(number);
        System.out.println(status);
    }

}
