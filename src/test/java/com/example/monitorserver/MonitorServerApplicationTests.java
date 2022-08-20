package com.example.monitorserver;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.controller.ProjectController;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.*;
import com.example.monitorserver.utils.NettyEventGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@SpringBootTest
@Slf4j
class MonitorServerApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectController projectController;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;



    @Autowired
    private com.example.monitorserver.service.apiErrorService apiErrorService;

    @Autowired
    private JsErrorService jsErrorService;

    @Autowired
    private BlankErrorService blankErrorService;

    @Autowired
    private ResourceErrorService resourceErrorService;

    @Autowired
    private PerformanceErrorService performanceErrorService;

    @Test
    void Test() throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = NettyEventGroup.group;

        /*Future<Long> JsFuture = group.next().submit(() -> jsErrorService.getJsErrorCount("Jiao"));
        Future<Long> ApiFuture = group.next().submit(() -> apiErrorService.getApiCount("Jiao"));
        Future<Long> BlankFuture = group.next().submit(() -> blankErrorService.getBlankCount("Jiao"));
        Future<Long> ResFuture = group.next().submit(() -> resourceErrorService.getResourceCount("Jiao"));

        Long jsCount = JsFuture.get();
        Long apiCount = ApiFuture.get();
        Long blankCount = ApiFuture.get();
        Long resourceCount = ResFuture.get();
        Long whole = apiCount + blankCount + resourceCount;
        double rate = 1.000*jsCount / whole;
        String  str = String.format("%.2f",rate );
        rate = Double.parseDouble(str);*/

/*

        Map<String, Object> map1 = new HashMap<>();
        map1.put("project_name", "FuriKuri01");
        Result byCondition = projectService.getByCondition(map1);
        List<Project> projects1 = (List<Project>) byCondition.getData();
        Project next1 = projects1.iterator().next();
        String username1 = next1.getUsername();
        log.debug(username1);
        Map<String, Object> condition = new HashMap<>();
        condition.put("username", username1);
        Result byCondition1 = userService.getByCondition(condition);
        List<User> userList = (List<User>) byCondition1.getData();
        User next2 = userList.iterator().next();
        String username2 = next2.getUsername();
        log.debug(username2);
*/

        Future<Result> future = group.next().submit(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("project_name", "FuriKuri01");
            return projectService.getByCondition(map);
        });

        Future<Result> userFuture = group.next().submit(() -> {
            List<Project> projects = (List<Project>) future.get().getData();
            Project project = projects.iterator().next();
            String username = project.getUsername();
            log.debug(username);
            Map<String, Object> condition = new HashMap<>();
            condition.put("username", username);
            return userService.getByCondition(condition);
        });
        List<User> userList = (List<User>) userFuture.get().getData();
        User next = userList.iterator().next();
        log.debug(next.getUsername());

      /*  Future<List<Project>> userFuture = future.addListener(new GenericFutureListener<Future<? super List<Project>>>() {
            @Override
            public void operationComplete(Future<? super List<Project>> future) throws Exception {
                List<Project> projects = (List<Project>) future.get();
                Project project = projects.iterator().next();
                Map<String, Object> map = new HashMap<>();
                map.put("user_id", project.getUserId());
                userService.getByCondition(map);
            }
        });*/


    }

    @Test
    void Test2(){
        Set<String> keys = redisTemplate.keys(RedisEnum.INDEX_KEY.getMsg());
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()){
            redisTemplate.delete(iterator.next());
        }
    }



//
//    @Test
//    void userTest() {
////        DynamicTableNameConfig.setDynamicTableName("user");
//        /*List<User> users = userMapper.selectList(null);
//        Iterator<User> iterator = users.iterator();
//        while(iterator.hasNext()){
//            User user = iterator.next();
//            String userId = user.getUserId();
//            System.out.println(userId);
//        }*/
//    }
//
//    @Test
//    void projectTest(){
//        /*Project project = new Project();
//        project.setProjectName("Jiao");
//        project.setProjectDesc("Jiao");
//        project.setProjectUrl("Jiao");
//        Result result = projectController.saveProject(project);*/
//        Result result = projectService.getPageProject(1, 5, 9);
//        System.out.println(result.toString());
//    }
//
//    @Autowired
//    private LogService logService;
//    @Test
//    void LogTest(){
//        logService.createTable();
//
//        //logService.select(null);
//        String str = "{'projectUrl':'106.13.18.48','outParameters':{'code':60401,'data':{'email':'111111111@qq.com','password':'','permission':1,'phone_number':'13619209420','user_id':5,'username':'zhangsan666'},'msg':'查询用户成功'},'traits':['8'],'method':'getByUsername','inParameters':{'username':'[zhangsan666]'},'responseTime':411,'ip':'192.168.190.1','visitDate':1660268459269,'packageName':'com.qgstudio.controller.UserController','uri':'/users/username'}";
//        Log logs = JSON.parseObject(str, Log.class);
//        logService.insert(logs);
//
//    }
//
//    @Test
//    void intTest(){
//        //TODO 1.获得现在的小时数
//        SimpleDateFormat sdf = new SimpleDateFormat("HH");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//
//        Integer now = Integer.valueOf(sdf.format(calendar.getTime()));
//        //TODO 2.从0时开始获取统计数据
//        int count = (~(now-1));
//        System.out.println(count);
//    }
//
//    @Autowired
//    private StatisticsMapper statisticsMapper;
//    @Test
//    void sumTest(){
//        QueryWrapper<Statistics> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("SUM(views) AS views","SUM(visits) AS visits","SUM(defeat) AS defeat");
//        //,"SUM(visits) AS visits","SUM(defeat) AS defeat"
//        MybatisConfig.setDynamicTableName("t_statistics_20220812_18");
//        List<Statistics> list = statisticsMapper.selectList(queryWrapper);
//        Statistics one = statisticsMapper.selectOne(queryWrapper);
//        //System.out.println(list);;
//        System.out.println(one);
//    }
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
//    @Test
//    void ManageTest(){
//        HashMap<String,Object> map = new HashMap<>();
//        map.put("1","1");
//        redisTemplate.opsForHash().putAll("王健豪",map);
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries("王健豪");
//        redisTemplate.opsForHash().putAll("王健豪",map);
//        Iterator<String> iterator = redisTemplate.keys("王".concat("*")).iterator();
//        while (iterator.hasNext()){
//            System.out.println("redis中缓存的key"+iterator.next());
//        }
//        redisTemplate.opsForHash().putAll("王健豪",map);
//        Iterator<String> iterator2 = redisTemplate.keys("王".concat("*")).iterator();
//        while (iterator2.hasNext()){
//            System.out.println("redis中缓存的key"+iterator2.next());
//        }
//    }
//
//    @Test
//    void DateTest() {
//        /*LocalDateTime dateTime = LocalDateTime.now();
//        long now = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        LocalDateTime time = dateTime.minusDays(1);
//        long last = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        System.out.println(now>last);*/
//
//        /*Iterator<String> iterator = redisTemplate.keys(RedisEnum.LOGIN_TOKEN.getMsg().concat("*")).iterator();
//        while (iterator.hasNext()){
//            redisTemplate.delete(iterator.next());
//            }
//        }*/
//
//        String number = "1";
//        int status = Integer.parseInt(number);
//        System.out.println(status);
//    }

}
