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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "前端监控总览操作接口")
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
    @ApiOperation("前端SDK接受端口")
    public Result getSDK(@ApiParam(name = "type,data",value = "前端错误类型(JsError,BlankError,ResourceError,PerformanceError),根据type转相关实体类",required = true) @RequestBody SDK data){
        String type = data.getType();
        NioEventLoopGroup group = NettyEventGroup.group;
        switch (type){
            case "JsError":

                JsError JsError = JSON.parseObject(data.getData(), JsError.class);
                group.next().submit(()->jsErrorService.insert(JsError));

                group.next().submit(() -> {
                    if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+JsError.getProjectName()+"whole")){
                        Map<Object, Object> map = redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + JsError.getProjectName()+"whole");
                        //对缓存中的数据进行更新
                        Long thisWeekCount = (Long) map.get("jsThisWeekCount");
                        Map<String, Object> update = statistic((Long) map.get("total"), ++thisWeekCount, (Long) map.get("jsLastWeekCount"));
                        Map<String, Object> result = packageData(++thisWeekCount, (Long) map.get("jsLastWeekCount"), update.get("Rate"), update.get("IncrRate"), update.get("CountIncr"), "js");
                        map.putAll(result);
                    }
                });

                break;
            case "BlankError":
                BlankError blankError = JSON.parseObject(data.getData(), BlankError.class);
                blankErrorService.insert(blankError);
                break;
            case "ResourceError":
                ResourceError resourceError = JSON.parseObject(data.getData(), ResourceError.class);
                group.next().submit(()->resourceErrorService.insert(resourceError));
                group.next().submit(()->{
                    if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+resourceError.getProjectName()+"whole")){
                        Map<Object, Object> map = redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + resourceError.getProjectName()+"whole");
                        //对缓存中的数据进行更新
                        Long thisWeekCount = (Long) map.get("resThisWeekCount");
                        Map<String, Object> update = statistic((Long) map.get("total"), ++thisWeekCount, (Long) map.get("resLastWeekCount"));
                        Map<String, Object> result = packageData(++thisWeekCount, (Long) map.get("resLastWeekCount"), null, update.get("IncrRate"), update.get("CountIncr"), "res");
                        map.putAll(result);
                    }
                });
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
    @ApiOperation("总览信息js,api,resource信息获取")
    public Result whole(@ApiParam(name = "projectName",value = "项目名",required = true)@RequestBody Data data) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = NettyEventGroup.group;
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
        Long resLastWeekCount = (Long) resource.get("LastWeek");

        long ThisWeekWhole  = jsThisWeekCount + apiThisWeekCount + blankThisWeekCount + resThisWeekCount;

        Map<String,Object> result = new HashMap<>();
        result.put("total",ThisWeekWhole);

        // js类型
        Map<String, Object> jsSta = statistic(ThisWeekWhole, jsThisWeekCount, jsLastWeekCount);
        Map<String, Object> jsMap = packageData(jsThisWeekCount, jsLastWeekCount, jsSta.get("Rate"), jsSta.get("IncrRate"), jsSta.get("CountIncr"), "js");
        result.putAll(jsMap);

        // api类型
        Map<String, Object> apiSta = statistic(ThisWeekWhole, apiThisWeekCount, apiLastWeekCount);
        Map<String, Object> apiMap = packageData(apiThisWeekCount, apiLastWeekCount, apiSta.get("Rate"), apiSta.get("IncrRate"), apiSta.get("CountIncr"), "api");
        result.putAll(apiMap);

        // resource类型
        Map<String, Object> resourceSta = statistic(ThisWeekWhole, resThisWeekCount, resLastWeekCount);
        Map<String, Object> resMap = packageData(resThisWeekCount, resLastWeekCount, null, resourceSta.get("IncrRate"), resourceSta.get("CountIncr"), "api");
        result.putAll(resMap);


        redisTemplate.opsForHash().putAll(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"whole",result);
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"whole",1, TimeUnit.HOURS);
        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    private Map<String,Object> statistic(Long ThisWeekWhole,Long ThisWeekCount , Long LastWeekCount){
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

    private Map<String,Object> packageData(Object ThisWeekCount , Object LastWeekCount , Object Rate , Object IncrRate , Object CountIncr,String type){
        Map<String,Object> result = new HashMap<>();
        result.put(type+"ThisWeekCount",ThisWeekCount);
        result.put(type+"LastWeekCount",LastWeekCount);
        result.put(type+"Rate",Rate);
        result.put(type+"IncrRate",IncrRate);
        result.put(type+"CountIncr",CountIncr);
        return result;
    }
}
