package com.example.monitorserver.controller.server;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.mapper.apiErrorMapper;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.utils.NettyEventGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private apiErrorMapper apiErrorMapper;


    @PostConstruct
    public void init(){
        option = this;
        option.apiErrorService  = this.apiErrorService;
        option.projectService = this.projectService;
        option.redisTemplate = this.redisTemplate;
        option.apiErrorMapper = this.apiErrorMapper;
    }

    public static void MessageHandle(String message) throws Exception {
        log.debug("存储后台监控数据");
        NioEventLoopGroup group = NettyEventGroup.group;
        apiError error = JSON.parseObject(message, apiError.class);
        log.debug(error.toString());
        String projectName = option.projectService.getProjectName(error.getProjectUrl());
        error.setProjectName(projectName);


        // 对总览信息的缓存进行更新
        group.next().submit(()->option.apiErrorService.insert(error));
        //总览展示
        group.next().submit(()->{
            log.debug("更新总览信息");
            if (option.redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+projectName+"whole")){
                Map<Object, Object> map = option.redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + projectName+"whole");
                //对缓存中的数据进行更新
                Long thisWeekCount =  Long.valueOf(String.valueOf(map.get("apiThisWeekCount")));
                Map<String, Object> update = statistic( Long.valueOf(String.valueOf(map.get("total"))), ++thisWeekCount, (Long) map.get("apiLastWeekCount"));
                Map<String, Object> result = packageData(++thisWeekCount, Long.valueOf(String.valueOf(map.get("apiLastWeekCount"))), update.get("Rate"), update.get("IncrRate"), update.get("CountIncr"), "api");
                map.putAll(result);
                option.redisTemplate.opsForHash().putAll(RedisEnum.INDEX_KEY.getMsg()+projectName+"whole",map);
            }
        });
        //展示PV、UV
        group.next().submit(()->{
            log.debug("更新访问");
            if (option.redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+projectName+"visits")){
                Map<Object, Object> map = option.redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + projectName + "visits");
                Long pv = Long.valueOf(String.valueOf(map.get("PV")));
                log.debug("访问量："+pv);

                map.put("PV",++pv);
                String ip = error.getIp();
                QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("ip",ip);
                Long count = option.apiErrorMapper.selectCount(queryWrapper);
                if (count !=0){
                    Long uv = Long.valueOf(String.valueOf(map.get("UV")));
                    map.put("UV",++uv);
                }
                if (error.getException() != null){
                    Long defeat = Long.valueOf(String.valueOf(map.get("count")));
                    map.put("count",++defeat);
                    double percent =( 1 - 1.00 * defeat / count) * 100;
                    map.put("percent",percent);
                }
                option.redisTemplate.opsForHash().putAll(RedisEnum.INDEX_KEY.getMsg()+projectName+"visits",map);
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
