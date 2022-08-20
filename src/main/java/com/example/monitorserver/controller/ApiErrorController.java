package com.example.monitorserver.controller;


import cn.hutool.core.bean.BeanUtil;
import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.apiErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/apiError")
//@CrossOrigin(origins = "*")
public class ApiErrorController {

    @Autowired
    private apiErrorService apiErrorService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 服务器包监控日志
     * @return 返回包名packageName  访问量visits，访问人次visits_people，错误数defeatCount，成功率率rate，平均耗时avgResponseTime
     */
    @GetMapping("/serverPackage")
    @Secret
    public Result getPackageInfor(){
        return  apiErrorService.getPackageInfor();
    }

    /**
     * 获取服务端的接口日志访问量，访问人次，错误数，错误率
     * @param data 项目名projectName
     * @return 返回包名packageName  接口uri 访问量visits，访问人次visits_people，错误数defeatCount，成功率rate，平均耗时avgResponseTime
     */
    @PostMapping("/serverMethod")
    @Secret
    public Result getMethodInfor(@RequestBody Data data){
        return apiErrorService.getMethodInfor(data.getPackageName());
    }

    /**
     * @param data  项目名projectName 时间选择dateType
     * @return 返回该时间段下的api错误数，错误率deafCount，访问量visits，访问人次visits_people
     */
    @PostMapping("/err")
    @Secret
    public Result getApiErrorByType(@RequestBody Data data) throws ExecutionException, InterruptedException {
        if (redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"err")){
            List<apiError> apiError= (List<apiError>) redisTemplate.opsForList().rightPop(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName() + "err");
            return new Result(ResultEnum.REQUEST_SUCCESS,apiError);
        }
        Result result = apiErrorService.getApiErrByType(data.getProjectName(), data.getDateType());
        List<apiError> apiError = (List<apiError>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg() + data.getProjectName()+"err", apiError);
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+data.getProjectName()+"err",1, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 获取api各个方法下的错误数，错误率
     * @param data 项目名projectName
     * @return 返回method方法名 ， uri接口，rate错误率，avgResponseTime平均耗时
     */
    @PostMapping("/methodError")
    @Secret
    public Result getMethodError(@RequestBody Data data){

        return apiErrorService.selectMethod(data.getProjectName());
    }


    /**
     * 查看服务端日志
     * @param data method接口名
     * @return 返回所有的api详细信息
     */
    @PostMapping("/detail")
    @Secret
    public Result getDetail(@RequestBody Data data){
        return apiErrorService.getDetail(data.getMethod());
    }
}
