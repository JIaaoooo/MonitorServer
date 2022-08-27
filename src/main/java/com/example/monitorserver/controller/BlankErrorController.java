package com.example.monitorserver.controller;


import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.BlankError;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.BlankErrorService;
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
 * @program: monitor server
 * @description: 白屏错误执行层
 * @author: Jiao
 * @create: 2022-08-19 9：34
 */

@RestController
@RequestMapping("/blankError")
@CrossOrigin("http://localhost:3000")
@Api(tags = "白屏错误操作接口")
public class BlankErrorController {

    @Autowired
    private BlankErrorService blankErrorService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    /**
     * 根据时间粒度获取白屏错误数
     * @param data 项目名projectName，时间粒度type
     * @return 返回该项目该时间段下的错误数count，错误率percent,dataStr时间段
     */
    @PostMapping("/brr")
    @Secret
    @ApiOperation("根据不同的时间粒度获取白屏错误次数")
    public Result getBlankErrByType(@ApiParam(name = "projectName,type",value = "项目名，时间粒度选择",required = true) @RequestBody Data data){
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"blank")){
            List<BlankError> blankError = (List<BlankError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"blank");
            return new Result(ResultEnum.REQUEST_SUCCESS,blankError);
        }
        Result result = blankErrorService.getBlankErrByType(data.getProjectName(), data.getType());
        List<BlankError> blankError = (List<BlankError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"blank", blankError);
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"blank",1, TimeUnit.MINUTES);

        return result;
    }
}
