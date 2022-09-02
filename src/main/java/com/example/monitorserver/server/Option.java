package com.example.monitorserver.server;

import com.alibaba.fastjson.JSON;
import com.example.monitorserver.constant.Constants;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.exception.SystemException;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.utils.NettyEventGroup;
import com.example.monitorserver.utils.RSAUtil;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.crypto.CipherMode.decrypt;

/**
 * @program: monitor server
 * @description: 用于客户端信息传输交通枢纽
 * @author: Jiao
 * @create: 2022-08-12  13：30
 */
@Component
@Slf4j
public class Option {

    @Autowired
    private apiErrorService apiErrorService;


    @Autowired
    private ProjectService projectService;

    private static Option option;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @PostConstruct
    public void init(){
        option = this;
        option.apiErrorService  = this.apiErrorService;
        option.projectService = this.projectService;
        option.redisTemplate = this.redisTemplate;
    }

    public static void MessageHandle(String message) throws Exception {
        NioEventLoopGroup group = NettyEventGroup.group;
        apiError error = JSON.parseObject(message, apiError.class);
        String projectName = option.projectService.getProjectName(error.getProjectUrl());
        error.setProjectName(projectName);

        /*if(!error.getProjectUrl().equals("www.monitorServer.com") && !error.getUri().equals("/SDK")){
        }*/

        // 对总览信息的缓存进行更新
        group.next().submit(()->option.apiErrorService.insert(error));
        group.next().submit(()->{
            if (option.redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+projectName+"whole")){
                Map<Object, Object> map = option.redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + projectName+"whole");
                //对缓存中的数据进行更新
                Long thisWeekCount = (Long) map.get("apiThisWeekCount");
                Map<String, Object> update = statistic((Long) map.get("total"), ++thisWeekCount, (Long) map.get("apiLastWeekCount"));
                Map<String, Object> result = packageData(++thisWeekCount, (Long) map.get("apiLastWeekCount"), update.get("Rate"), update.get("IncrRate"), update.get("CountIncr"), "api");
                map.putAll(result);
            }
        });
    }

    private static Map<String,Object> statistic(Long ThisWeekWhole,Long ThisWeekCount , Long LastWeekCount){
        // 计算当周该错误的错误率
        Double Rate = 0.0;
        if (ThisWeekWhole!=0){
            Rate = 1.000*ThisWeekCount / ThisWeekWhole*100;
            String  str = String.format("%.2f",Rate);
            Rate = Double.parseDouble(str);
        }
        // 计算周同比增长
        Double IncrRate = ThisWeekCount * 100 *1.00;
        if (LastWeekCount!=0){
            IncrRate = 1.00*(ThisWeekCount - LastWeekCount)/LastWeekCount * 100;
            String  str = String.format("%.2f",IncrRate);
            IncrRate = Double.parseDouble(str);

        }
        Long CountIncr = ThisWeekCount - LastWeekCount;
        Map<String,Object> map = new HashMap<>();
        map.put("Rate",Rate);
        map.put("IncrRate",IncrRate);
        map.put("CountIncr",CountIncr);

        return map;
    }

    private static Map<String,Object> packageData(Object ThisWeekCount , Object LastWeekCount , Object Rate , Object IncrRate , Object CountIncr,String type){
        Map<String,Object> result = new HashMap<>();
        result.put(type+"ThisWeekCount",ThisWeekCount);
        result.put(type+"LastWeekCount",LastWeekCount);
        result.put(type+"Rate",Rate);
        result.put(type+"IncrRate",IncrRate);
        result.put(type+"CountIncr",CountIncr);
        return result;
    }
}
