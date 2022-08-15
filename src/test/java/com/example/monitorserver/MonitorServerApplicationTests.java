package com.example.monitorserver;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.mapper.StatisticsMapper;
import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.controller.ProjectController;
import com.example.monitorserver.po.Log;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.LogService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserService;
import com.example.monitorserver.utils.MapBeanUtil;
import com.example.monitorserver.utils.MybatisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

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
        String str = "{'projectUrl':'106.13.18.48','outParameters':{'code':60401,'data':{'email':'111111111@qq.com','password':'','permission':1,'phone_number':'13619209420','user_id':5,'username':'zhangsan666'},'msg':'查询用户成功'},'traits':['8'],'method':'getByUsername','inParameters':{'username':'[zhangsan666]'},'responseTime':411,'ip':'192.168.190.1','visitDate':1660268459269,'packageName':'com.qgstudio.controller.UserController','uri':'/users/username'}";
        Log logs = JSON.parseObject(str, Log.class);
        logService.insert(logs);

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
    private StatisticsMapper statisticsMapper;
    @Test
    void sumTest(){
        QueryWrapper<Statistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("SUM(views) AS views","SUM(visits) AS visits","SUM(defeat) AS defeat");
        //,"SUM(visits) AS visits","SUM(defeat) AS defeat"
        MybatisConfig.setDynamicTableName("t_statistics_20220812_18");
        List<Statistics> list = statisticsMapper.selectList(queryWrapper);
        Statistics one = statisticsMapper.selectOne(queryWrapper);
        //System.out.println(list);;
        System.out.println(one);
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
    void DateTest(){
        String data = "2002-12-31 00:00:00";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(data, dtf);
        System.out.println(dateTime);
        /*DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//2.要转换的对象
        LocalDateTime time = LocalDateTime.now();

//3.发动功能
        String localTime = df.format(time);
        System.out.println("LocalDateTime转成String类型的时间："+localTime);

//3.LocalDate发动，将字符串转换成  df格式的LocalDateTime对象，的功能
        LocalDateTime LocalTime = LocalDateTime.parse(localTime,df);
        System.out.println("String类型的时间转成LocalDateTime："+LocalTime);*/
    }
}
