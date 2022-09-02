package com.example.monitorserver;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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


    @Autowired
    private UserMapper userMapper;

    @Test
    void Test2(){
        Map<String,Object> map1 = new HashMap<>();
        map1.put("a","a");
        Map<String,Object> map2 = new HashMap<>();
        map2.put("a","b");
        map1.putAll(map2);
        System.out.println(map1.get("a"));

    }



}
