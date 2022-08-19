package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.mapper.apiErrorMapper;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.utils.NettyEventGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class apiErrorServiceImpl extends ServiceImpl<apiErrorMapper, apiError> implements apiErrorService {

    @Autowired
    private apiErrorMapper apiErrorMapper;

    @Override
    public void insert(apiError apiError) {
        apiErrorMapper.insert(apiError);
    }

    @Override
    public Result selectMethod(String projectName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        List<apiError> list = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = list.iterator();
        List<apiError> results = new ArrayList<>();
        while(iterator.hasNext()){
            apiError apiError = iterator.next();
            //查询方法的总访问量
            queryWrapper.eq("method",apiError.getMethod());
            Long sum = apiErrorMapper.selectCount(queryWrapper);
            if (sum==0){
                continue;
            }
            //获取该方法的错误数
            queryWrapper.isNotNull("exception");
            Long defeat = apiErrorMapper.selectCount(queryWrapper);
            queryWrapper.clear();
            queryWrapper.eq("project_name",projectName)
                    .eq("method",apiError.getMethod())
                    .select("SUM(response_time) AS response_time");
            apiError apiError1 = apiErrorMapper.selectOne(queryWrapper);
            Long responseTime = apiError1.getResponseTime();
            //平均耗时
            double AvgTime = 1.000 *responseTime / sum;
            // 错误率
            double percent = 1.000 * defeat / sum ;
            apiError result = new apiError()
                    .setRate(percent)
                    .setAvgResponseTime(AvgTime);
            results.add(result);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS,results);

    }

    @Override
    public void update(apiError apiError) {
        UpdateWrapper<apiError> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("project_name",apiError.getProjectName());
        apiErrorMapper.update(apiError,updateWrapper);

    }

    @Override
    public Result getWhole(String project_name) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        // TODO 1.获取访问量
        queryWrapper.eq("project_name",project_name);
        Long count = apiErrorMapper.selectCount(queryWrapper);
        // TODO 2.获取异常量
        queryWrapper.ne("exception",null);
        Long exception = apiErrorMapper.selectCount(queryWrapper);
        // TODO 3.计算成功率
        double deaRate  = 1.0*exception/count;
        double rate = 1 - deaRate;
        apiError apiError = new apiError()
                .setRate(rate)
                .setDefeatCount(exception);
        return new Result(ResultEnum.REQUEST_FALSE,apiError);
    }

    @Override
    public Result each(String project_name) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT method")
                .eq("project_name",project_name);
        List<apiError> result = new ArrayList<>();
        // 获取各个接口信息
        List<apiError> apiErrors = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = apiErrors.iterator();
        while(iterator.hasNext()){
            apiError error = iterator.next();
            // 获得接口名称
            String method = error.getMethod();
            QueryWrapper<apiError> qw = new QueryWrapper<>();
            qw.eq("method",method);
            // 计算错误率  获取该接口的api总信息梁
            Long count = apiErrorMapper.selectCount(qw);
            // 获取接口异常数
            qw.ne("exception",null);
            Long exception = apiErrorMapper.selectCount(qw);
            //计算错误率
            double deaRate  = 1.0*exception/count;
            // 获取该接口的平均访问耗时
            QueryWrapper<apiError> qw2 = new QueryWrapper<>();
            qw.eq("method",method)
                    .select("SUM (response_time) AS response_time");
            apiError apiError = apiErrorMapper.selectOne(qw2);
            Long responseTime = apiError.getResponseTime();
            // 计算平均耗时
            Long AvgResponseTime = responseTime / count;
            apiError TheError = new apiError()
                    .setRate(deaRate)
                    .setAvgResponseTime(AvgResponseTime);
            // 存入集合
            result.add(TheError);
        }

        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    @Override
    public Long getApiCount(String projectName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        return apiErrorMapper.selectCount(queryWrapper);
    }

    @Override
    public Result getPackageInfor() {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT package_name")
                .eq("project_url","www.monitorServer.com");
        List<apiError> logs = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = logs.iterator();
        List<apiError> result = new ArrayList<>();
        // TODO 获取的到服务器所有的包名
        while (iterator.hasNext()){
            String packageName = iterator.next().getPackageName();
            //TODO 通过包名获取访问量，访问人次，异常数，成功率
            QueryWrapper<apiError> qw = new QueryWrapper<>();
            qw.eq("project_url","www.monitorServer.com")
                    .eq("package_name",packageName);
            //总访问量
            Long visits = apiErrorMapper.selectCount(qw);
            qw.select("DISTINCT ip");
            Long visits_people = apiErrorMapper.selectCount(qw);
            QueryWrapper<apiError> qw2 = new QueryWrapper<>();
            qw2.eq("project_url","www.monitorServer.com")
                    .eq("package_name",packageName)
                    .ne("exception",null);
            Long defeatCount = apiErrorMapper.selectCount(qw2);
            double rate = 1.0 * defeatCount / visits;
            apiError apiError = new apiError()
                    .setPackageName(packageName)
                    .setDefeatCount(defeatCount)
                    .setRate(rate)
                    .setVisits(visits)
                    .setVisits_people(visits_people);
            result.add(apiError);
        }

        // TODO 更新信息表
        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    @Override
    public Result getMethodInfor(String packageName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT uri")
                .eq("project_url","www.monitorServer.com")
                .eq("package_name",packageName);
        List<apiError> logs = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = logs.iterator();
        List<apiError> result = new ArrayList<>();
        // TODO 得到该包下的各个接口信息
        while (iterator.hasNext()){
            String uri = iterator.next().getUri();
            QueryWrapper<apiError> qw = new QueryWrapper<>();
            qw.eq("project_url","www.monitorServer.com")
                    .eq("package_name",packageName)
                    .eq("uri",uri);
            //总访问量
            Long visits = apiErrorMapper.selectCount(qw);
            qw.select("DISTINCT ip");
            Long visits_people = apiErrorMapper.selectCount(qw);
            QueryWrapper<apiError> qw2 = new QueryWrapper<>();
            qw2.eq("project_url","www.monitorServer.com")
                    .eq("package_name",packageName)
                    .eq("package_name",packageName)
                    .ne("exception",null);
            Long defeatCount = apiErrorMapper.selectCount(qw2);
            double rate = 1.0 * defeatCount / visits;
            apiError apiError = new apiError()
                    .setPackageName(packageName)
                    .setUri(uri)
                    .setDefeatCount(defeatCount)
                    .setRate(rate)
                    .setVisits(visits)
                    .setVisits_people(visits_people);
            result.add(apiError);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    @Override
    public Result getApiErrByType(String projectName, String type) {


        //需要根据项目名和类型进行查询日志
        //type:1:日,查询24从现在起前24小时的错误数量,
        //2:月:查询从现在起,4个星期的错误数量
        //3.年:查询从现在起,12个月的错误数

        switch (type) {
            case "1":
                return new Result(getApiErrHourCount(projectName));
            case "2":
                return new Result(getApiErrDayCount(projectName));
            case "3":
                return new Result(getApiErrMonthCount(projectName));
            default:
                return null;
        }
    }

    private List<apiError> getApiErrHourCount(String projectName)  {
        String pattern = "yyyy-MM-dd HH";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<apiError> data = new LinkedList<>();
        apiError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();
        time = time.plusHours(1);
        //往前查询

        Long visitsSum = 0L;

        for (int i = 0; i < 4; i++) {

            LambdaQueryWrapper<apiError> lqw = new LambdaQueryWrapper<>();
            lqw.eq(apiError::getProjectName, projectName)
                    .isNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusHours(-6));
            Long count = apiErrorMapper.selectCount(lqw);

            LambdaQueryWrapper<apiError> lqw1 =  new LambdaQueryWrapper<>();
            lqw1.eq(apiError::getProjectName, projectName)
                    .isNotNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusHours(-6));
            Long deafCount = apiErrorMapper.selectCount(lqw1);
            vo = new apiError()
                    .setCount(count)
                    .setDefeatCount(deafCount);
            vo.setDateStr(time.plusHours(-6).getHour() + "时-" +time.getHour()+"时");
            data.add(vo);
            visitsSum += count;

            time  = time.plusHours(-6);
        }

        return getPercent(data, visitsSum);

    }


    private List<apiError> getApiErrDayCount(String projectName) {
        String pattern = "yyyy-MM-dd HH";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<apiError> data = new LinkedList<>();
        apiError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();
        //往前查询

        Long visits = null;
        Long visitsSum = 0L;
        Long defeatSum = 0L;

        for (int i = 0; i < 4; i++) {

            LambdaQueryWrapper<apiError> lqw =  new LambdaQueryWrapper<>();
            lqw.eq(apiError::getProjectName, projectName)
                    .isNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusDays(-7));
            Long count = apiErrorMapper.selectCount(lqw);

            LambdaQueryWrapper<apiError> lqw1 =  new LambdaQueryWrapper<>();
            lqw1.eq(apiError::getProjectName, projectName)
                    .isNotNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusDays(-7));
            Long deafCount = apiErrorMapper.selectCount(lqw1);
            vo = new apiError()
                    .setCount(count)
                    .setDefeatCount(deafCount);
            vo.setDateStr(time.plusDays(-7).getDayOfMonth() + "日-" +time.getDayOfMonth()+"日");
            data.add(vo);
            visitsSum += count;

            time  = time.plusDays(-7);
        }

        return getPercent(data, visitsSum);

    }

    private List<apiError> getApiErrMonthCount(String projectName)  {
        String pattern = "yyyy-MM-dd HH";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        //需要查询24小时,所以需要查询到个数据进行返回
        List<apiError> data = new LinkedList<>();
        apiError vo = null;

        //获得当前时间2021年6月9日14小时49分
        LocalDateTime time = LocalDateTime.now();
        //往前查询

        Long visitsSum = 0L;

        for (int i = 0; i < 4; i++) {

            LambdaQueryWrapper<apiError> lqw =  new LambdaQueryWrapper<>();
            lqw.eq(apiError::getProjectName, projectName)
                    .isNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusMonths(-3));
            Long count = apiErrorMapper.selectCount(lqw);

            LambdaQueryWrapper<apiError> lqw1 =  new LambdaQueryWrapper<>();
            lqw1.eq(apiError::getProjectName, projectName)
                    .isNotNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusMonths(-3));
            Long deafCount = apiErrorMapper.selectCount(lqw1);
            vo = new apiError()
                    .setCount(count)
                    .setDefeatCount(deafCount);
            vo.setDateStr(time.plusMonths(-3).getMonthValue() + "月-" + time.getMonthValue() + "月");
            data.add(vo);
            visitsSum += count;

            time = time.plusMonths(-3);
        }

        return getPercent(data, visitsSum);
    }

        private List<apiError> getPercent(List<apiError> data, Long sum) {
        double percent;
        for (apiError datum : data) {
            percent = 1 - datum.getCount() * 100.0 / sum ;
            String  str = String.format("%.2f",percent);
            percent = Double.parseDouble(str);
            datum.setPercent(percent);
        }
        return data;
    }

}
