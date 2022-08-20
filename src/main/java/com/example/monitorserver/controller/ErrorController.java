package com.example.monitorserver.controller;


import com.alibaba.fastjson.JSON;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.BlankErrorService;
import com.example.monitorserver.service.JsErrorService;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.service.PerformanceErrorService;
import com.example.monitorserver.service.ResourceErrorService;

import com.example.monitorserver.utils.NettyEventGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @program: monitor server
 * @description: 前端监控接受
 * @author: Jiao
 * @create: 2022-08-13  20：36
 */
@RestController
@RequestMapping("/SDK")
@CrossOrigin(origins = "*")
@Slf4j
public class ErrorController {

    @Autowired
    HttpServletRequest request;


    @Autowired
    HttpServletResponse response;

    @Autowired
    private apiErrorService apiErrorService;

    @Autowired
    private JsErrorService jsErrorService;

    @Autowired
    private BlankErrorService blankErrorService;

    @Autowired
    private ResourceErrorService resourceErrorService;

    @Autowired
    private PerformanceErrorService performanceErrorService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;



    /**
     * 接受前端SDK信息分类处理
     * @param data type：前端错误类型  data泛型类转各种错误类型
     * @return 返回执行成功与否
     */
    @PostMapping
    public Result getSDK(@RequestBody SDK data){
        String type = data.getType();
        System.out.println("data = " + data);
        switch (type){
            case "JsError":
                apiError apiError = JSON.parseObject(data.getData(), apiError.class);
                apiErrorService.insert(apiError);
                break;
            case "BlankError":
                BlankError blankError = JSON.parseObject(data.getData(), BlankError.class);
                blankErrorService.insert(blankError);
                break;
            case "ResourceError":
                ResourceError resourceError = JSON.parseObject(data.getData(), ResourceError.class);
                resourceErrorService.insert(resourceError);
                break;
            case "PerformanceError":
                PerformanceError performanceError = JSON.parseObject(data.getData(), PerformanceError.class);
                performanceErrorService.insert(performanceError);
                break;
            default:
                return new Result(ResultEnum.REQUEST_FALSE);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    /**
     * 总览信息
     * @param data 项目名projectName
     * @return 返回比例 ：xxxThisWeekCount错误数 xxxThisWeekDefeatRate错误率  xxxCountIncreRate同比增长率  xxCountIncre错误增长数
     */
    @PostMapping("/whole")
    @Secret
    public Result whole(@RequestBody Data data) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = NettyEventGroup.group;
        Map<String,Object> map = new HashMap<>();
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"whole")){
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"whole");
            return new Result(ResultEnum.REQUEST_SUCCESS,entries);
        }

        Future<Result> JsFuture = group.next().submit(() -> jsErrorService.getJsErrorCount(data.getProjectName()));
        Future<Result> ApiFuture = group.next().submit(() -> apiErrorService.getApiCount(data.getProjectName()));
        Future<Result> BlankFuture = group.next().submit(() -> blankErrorService.getBlankCount(data.getProjectName()));
        Future<Result> ResFuture = group.next().submit(() -> resourceErrorService.getResourceCount(data.getProjectName()));

        Map<String,Object> js = (Map<String, Object>) JsFuture.get().getData();
        Map<String,Object> api = (Map<String, Object>) ApiFuture.get().getData();
        Map<String,Object> blank = (Map<String, Object>) BlankFuture.get().getData();
        Map<String,Object> resource = (Map<String, Object>) ResFuture.get().getData();

        //获取本周数据
        Long jsThisWeekCount = (Long) js.get("ThisWeek");
        Long apiThisWeekCount = (Long) api.get("ThisWeek");
        Long blankThisWeekCount = (Long) blank.get("ThisWeek");
        Long resThisWeekCount = (Long) resource.get("ThisWeek");

        //获取上周信息
        Long jsLastWeekCount = (Long) js.get("LastWeek");
        Long apiLastWeekCount = (Long) api.get("LastWeek");
        Long blankLastWeekCount = (Long) blank.get("LastWeek");
        Long resLastWeekCount = (Long) resource.get("LastWeek");

        long ThisWeekWhole  = jsThisWeekCount + apiThisWeekCount + blankThisWeekCount + resThisWeekCount;
        Double JsThisWeekDefeatRate = 0.0;
        Double ApiThisWeekDefeatRate = 0.0;
        if (ThisWeekWhole!=0){
             JsThisWeekDefeatRate = 1.000*jsThisWeekCount / ThisWeekWhole;
            String  str = String.format("%.2f",JsThisWeekDefeatRate);
            JsThisWeekDefeatRate = Double.parseDouble(str);
             ApiThisWeekDefeatRate = 1.000* apiThisWeekCount / ThisWeekWhole;
            String  str1 = String.format("%.2f",ApiThisWeekDefeatRate);
            ApiThisWeekDefeatRate = Double.parseDouble(str1);
        }



        map.put("apiThisWeekCount",apiThisWeekCount);
        map.put("ApiThisWeekDefeatRate",ApiThisWeekDefeatRate);
        map.put("jsThisWeekCount",jsThisWeekCount);
        map.put("JsThisWeekDefeatRate",JsThisWeekDefeatRate);
        map.put("resourceThisWeekCount",resThisWeekCount);

        Double jsCountIncreRate = 0.0;
        Double apiCountIncreRate = 0.0;
        Double resourceCountIncreRate = 0.0;
        if (jsLastWeekCount!=0){
            jsCountIncreRate = 1.00*(jsThisWeekCount-jsLastWeekCount)/jsLastWeekCount*100;
            apiCountIncreRate = 1.00*(apiThisWeekCount-apiLastWeekCount)/apiLastWeekCount*100;
            resourceCountIncreRate = 1.00*(resThisWeekCount-resLastWeekCount)/apiLastWeekCount*100;
        }
        map.put("jsCountIncreRate",jsCountIncreRate);
        map.put("apiCountIncreRate",apiCountIncreRate);
        map.put("jsCountIncre",jsThisWeekCount-jsLastWeekCount);
        map.put("apiCountIncre",apiThisWeekCount-apiLastWeekCount);


        map.put("resourceCountIncreRate",resourceCountIncreRate);
        map.put("resourceCountIncre",resThisWeekCount-resLastWeekCount);
        redisTemplate.opsForHash().putAll(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"whole",map);
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"whole",1, TimeUnit.MINUTES);
        return new Result(ResultEnum.REQUEST_SUCCESS,map);
    }


}
