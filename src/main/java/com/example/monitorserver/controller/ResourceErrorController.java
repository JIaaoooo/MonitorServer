package com.example.monitorserver.controller;

import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ResourceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-08-18 15:58
 **/
@RestController
@RequestMapping("/resource")
@CrossOrigin(origins = "*")
public class ResourceErrorController {
    @Autowired
    private ResourceErrorService resourceErrorService;


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
    public Result getCountByProject(@RequestBody Data data) {
        return resourceErrorService.getCountByProject(data.getProjectName());
    }


    /**
     * 分时间段获得错误数
     * @param data 项目名projectName，时间段选择dateType，错误类型选择type
     * @return 返回在此时间段下的该错误类型的错误数、错误率
     */
    @PostMapping("/err")
    @Secret
    public Result getResErrByType(@RequestBody Data data) {
        return resourceErrorService.getErrByType(data.getProjectName(), data.getDateType(),data.getType());
    }

    /**
     * 获得资源错误总数
     * @param data 项目名projectName
     * @return 返回该项目资源错误总数
     */
    @PostMapping("/crr")
    @Secret
    public Result getResourceCount(@RequestBody Data data){

        Map<String,Object> result   = (Map<String, Object>) resourceErrorService.getResourceCount(data.getProjectName()).getData();
        return new Result(ResultEnum.REQUEST_SUCCESS,result.get("ThisWeek"));
    }
}

