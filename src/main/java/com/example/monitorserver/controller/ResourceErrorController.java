package com.example.monitorserver.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.PerformanceError;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ResourceErrorService;
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
 * @create: 2022-08-18 15:58
 **/
@RestController
@RequestMapping("/resource")
@CrossOrigin(origins = "*")
@Api(tags = "资源错误")
public class ResourceErrorController {
    @Autowired
    private ResourceErrorService resourceErrorService;


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 通过项目名获取各个错误标签的错误路径,表
     * @param data:项目名ProjectName
     * @return :封装了错误路径的结果集
     */
    @PostMapping("/brr")
    @Secret
    public Result getFileNameByProject(@RequestBody Data data) {

        return resourceErrorService.getFileNameByProject(data.getProjectName());
    }


    /**
     * 通过项目名各个错误的错误数和比例,图
     * @param data :项目名projectName
     * @return :封装了错误路径的结果集
     */
    @PostMapping("/count")
    @Secret
    @ApiOperation("通过项目名各个错误的错误数和比例,图")
    public Result getCountByProject(@ApiParam(name = "projectName",value = "项目名",required = true)@RequestBody Data data) {
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"count")){
            List<ResourceError> resourceError = (List<ResourceError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName() + "count");
            return new Result(ResultEnum.REQUEST_SUCCESS,resourceError);
        }
        Result result = resourceErrorService.getCountByProject(data.getProjectName());
        List<ResourceError> resourceError = (List<ResourceError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"count", resourceError);
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"count",1, TimeUnit.MINUTES);
        return result;
    }


    /**
     * 分时间段获得错误数
     * @param data 项目名projectName，时间段选择dateType，错误类型选择type
     * @return 返回在此时间段下的该错误类型的错误数、错误率
     */
    @PostMapping("/err")
    @Secret
    @ApiOperation("分时间段获得错误数")
    public Result getResErrByType(@ApiParam(name = "projectName,dateType,type",value = "项目名,时间段选择,错误类型选择",required = true)@RequestBody Data data) {
        return resourceErrorService.getErrByType(data.getProjectName(), data.getDateType(),data.getType());
    }

    /**
     * 获得资源错误总数
     * @param data 项目名projectName
     * @return 返回该项目资源错误总数
     */
    @PostMapping("/crr")
    @Secret
    @ApiOperation("获得资源错误总数")
    public Result getResourceCount(@ApiParam(name = "projectName",value = "项目名",required = true)@RequestBody Data data){

        Map<String,Object> result   = (Map<String, Object>) resourceErrorService.getResourceCount(data.getProjectName()).getData();
        return new Result(ResultEnum.REQUEST_SUCCESS,result.get("ThisWeek"));
    }
}

