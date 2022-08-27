package com.example.monitorserver.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.JsErrorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-17 16:38
 **/
@RestController
@RequestMapping("/jsError")
@CrossOrigin("http://localhost:3000")
@Api(tags = "Js错误接口")
public class JsErrorController {
    @Autowired
    private JsErrorService jsErrorService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 获取某个项目的各个时间段下的js报错信息,图的
     * @param data :项目名projectName项目名，日期选择type
     * @return :封装了报错信息的结果集，错误数：count  错误率：percent,dataStr时间段
     */
    @PostMapping("/err")
    @Secret
    @ApiOperation("取某个项目的各个时间段下的js报错信息")
    public Result getJsErrByType(@ApiParam(name = "projectName,dateType,type",value = "项目名,时间段选择,错误类型选择",required = true)@RequestBody Data data) {
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"err")){
            List<JsError> jsError  = (List<JsError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName() + "err");
            return new Result(ResultEnum.REQUEST_SUCCESS,jsError);
        }
        Result result = jsErrorService.getJsErrByType(data.getProjectName(), data.getType());
        List<JsError> jsError = (List<JsError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"err", jsError );
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"err",1, TimeUnit.MINUTES);
        return result;
    }

    /**
     * 获取某个项目各个url的错误信息，表
     * @param data:项目名projectName
     * @return :返回封装了错误信息的结果集,错误数：count  错误率：percent , dataStr时间段
     */
    @PostMapping("/urlErr")
    @Secret
    public Result getUrlErrCountByName(@ApiParam(name = "projectName", value = "项目名")@RequestBody Data data) {
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"urlErr")){
            List<JsError> jsError= (List<JsError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName() + "urlErr");
            return new Result(ResultEnum.REQUEST_SUCCESS,jsError);
        }
        Result result = jsErrorService.getUrlErrCountByName(data.getProjectName());
        List<JsError> jsError = (List<JsError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"urlErr", jsError );
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"urlErr",1, TimeUnit.MINUTES);
        return result;
    }

}
