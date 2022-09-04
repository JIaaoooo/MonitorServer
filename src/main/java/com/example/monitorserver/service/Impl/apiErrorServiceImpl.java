package com.example.monitorserver.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.RedisEnum;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class apiErrorServiceImpl extends ServiceImpl<apiErrorMapper, apiError> implements apiErrorService {

    @Autowired
    private apiErrorMapper apiErrorMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void insert(apiError apiError) {
        apiErrorMapper.insert(apiError);
    }

    @Override
    public Result selectMethod(String projectName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName)
                .select("DISTINCT method , uri");
        List<apiError> list = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = list.iterator();
        List<apiError> results = new ArrayList<>();
        while(iterator.hasNext()){
            apiError apiError = iterator.next();
            //查询方法的总访问量
            queryWrapper.clear();
            queryWrapper.eq("project_name",projectName)
                .eq("method",apiError.getMethod());
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
            String  str = String.format("%.2f",AvgTime);
            AvgTime = Double.parseDouble(str);
            // 错误率
            double percent = 1.000 * defeat / sum ;
            String  str1 = String.format("%.2f",percent);
            percent = Double.parseDouble(str1);
            apiError result = new apiError()
                    .setMethod(apiError.getMethod())
                    .setUri(apiError.getUri())
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
    public apiError getWhole(String project_name) {
        if(redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()+ project_name + "visits")){
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisEnum.INDEX_KEY.getMsg() + project_name + "visits");
            return BeanUtil.mapToBean(entries,apiError.class,false,new CopyOptions());
        }
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        // TODO 1.获取访问量
        queryWrapper.eq("project_name",project_name);
        Long count = apiErrorMapper.selectCount(queryWrapper);
        // TODO 2.获取访问人次
        queryWrapper.select("DISTINCT ip");
        Long uv = apiErrorMapper.selectCount(queryWrapper);
        queryWrapper.clear();
        // TODO 3.获取异常数
        queryWrapper.eq("project_name",project_name)
                .isNotNull("exception");
        Long defeat = apiErrorMapper.selectCount(queryWrapper);
        double percent =( 1 - 1.00 * defeat / count) * 100;
        String  str = String.format("%.2f",percent);
        percent = Double.parseDouble(str);
        apiError apiError = new apiError()
                .setPV(count)
                .setRate(percent)
                .setCount(defeat)
                .setUV(uv);
        redisTemplate.opsForHash().putAll(RedisEnum.INDEX_KEY.getMsg()+ project_name + "visits", BeanUtil.beanToMap(apiError));
        redisTemplate.expire(RedisEnum.INDEX_KEY.getMsg()+ project_name + "visits",1, TimeUnit.HOURS);
        return apiError;
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
            // 计算错误率  获取该接口的api总信息量
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
            Double AvgResponseTime = (double) (responseTime / count);
            apiError TheError = new apiError()
                    .setRate(deaRate)
                    .setAvgResponseTime(AvgResponseTime);
            // 存入集合
            result.add(TheError);
        }

        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    @Override
    public Result getApiCount(String projectName) {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        LocalDateTime time = LocalDateTime.now();
        queryWrapper.eq("project_name",projectName)
                .isNotNull("exception")
                .le("visit_date", time)
                .ge("visit_date", time.plusDays(-7));
        Long ThisWeek = apiErrorMapper.selectCount(queryWrapper);

        queryWrapper.clear();
        queryWrapper.eq("project_name",projectName)
                .isNotNull("exception")
                .le("visit_date", time.plusDays(-7))
                .ge("visit_date", time.plusDays(-14));
        Long LastWeek = apiErrorMapper.selectCount(queryWrapper);
        Map<String,Object> result = new HashMap<>();
        result.put("ThisWeek",ThisWeek);
        result.put("LastWeek",LastWeek);
        return new Result(result);
    }

    @Override
    public Result getPackageInfor() {
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT package_name")
                .eq("project_url","www.monitorServer.com");
        List<apiError> logs = apiErrorMapper.selectList(queryWrapper);
        Iterator<apiError> iterator = logs.iterator();
        List<apiError> result = new ArrayList<>();
        int i = 1;
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
                    .isNotNull("exception");
            Long defeatCount = apiErrorMapper.selectCount(qw2);
            //计算平均耗时
            qw2.clear();
            qw2.eq("project_url","www.monitorServer.com")
                    .eq("package_name",packageName);
            Long count = apiErrorMapper.selectCount(qw2);
            qw2.select("SUM(response_time) AS response_time");
            apiError one = apiErrorMapper.selectOne(qw2);
            Long responseTime = one.getResponseTime();
            Double AvgTime = responseTime* 1.000 / count;
            String  str = String.format("%.2f",AvgTime);
            AvgTime = Double.parseDouble(str);

            double rate =  1 - 1.000 * defeatCount / visits;
            String  str1 = String.format("%.2f",rate);
            rate = Double.parseDouble(str1);
            apiError apiError = new apiError()
                    .setId(i++)
                    .setPackageName(packageName)
                    .setDefeatCount(defeatCount)
                    .setRate(rate)
                    .setVisits(visits)
                    .setAvgResponseTime(AvgTime)
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
        int i =0;
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
                    .isNotNull("exception");
            Long defeatCount = apiErrorMapper.selectCount(qw2);
            //计算平均耗时
            qw2.clear();
            qw2.eq("project_url","www.monitorServer.com")
                    .eq("package_name",packageName);
            Long count = apiErrorMapper.selectCount(qw2);
            qw2.select("SUM(response_time) AS response_time");
            apiError one = apiErrorMapper.selectOne(qw2);
            Long responseTime = one.getResponseTime();
            Double AvgTime = responseTime* 1.000 / count;
            String  str = String.format("%.2f",AvgTime);
            AvgTime = Double.parseDouble(str);

            double rate =  1 - 1.0000 * defeatCount / visits;
            String  str1 = String.format("%.2f",rate);
            rate = Double.parseDouble(str1);
            apiError apiError = new apiError()
                    .setId(i++)
                    .setPackageName(packageName)
                    .setUri(uri)
                    .setDefeatCount(defeatCount)
                    .setRate(rate)
                    .setVisits(visits)
                    .setAvgResponseTime(AvgTime)
                    .setVisits_people(visits_people);
            result.add(apiError);
        }
        return new Result(ResultEnum.REQUEST_SUCCESS,result);
    }

    @Override
    public Result getDetail(String method,String projectName,int currentPage) {
        Page<apiError> page = new Page<>(currentPage,1);
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName)
                .eq("uri",method)
                .orderByDesc("visit_date");
        page = apiErrorMapper.selectPage(page,queryWrapper);
        List<apiError> records = page.getRecords();
        for (apiError record : records) {
            record.setCurrentPage(page.getCurrent());
            record.setPageSize(page.getTotal());

        }
        return new Result(ResultEnum.REQUEST_SUCCESS,records);
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

            lqw.clear();
            lqw.eq(apiError::getProjectName, projectName)
                    .isNotNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusHours(-6));
            Long deafCount = apiErrorMapper.selectCount(lqw);

            QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("project_name", projectName)
                    .select("DISTINCT ip")
                    .le("visit_date", time)
                    .ge("visit_date", time.plusHours(-6));
            Long visit_people = apiErrorMapper.selectCount(queryWrapper);

            vo = new apiError()
                    .setCount(count)
                    .setDefeatCount(deafCount)
                    //访问量
                    .setVisits(count+deafCount)
                    //访问人次
                    .setVisits_people(visit_people);
            vo.setDateStr(time.plusHours(-6).getHour() + "时-" +time.getHour()+"时");
            data.add(vo);

            time  = time.plusHours(-6);
        }

        return getPercent(data);

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

            QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("project_name", projectName)
                    .select("DISTINCT ip")
                    .le("visit_date", time)
                    .ge("visit_date", time.plusDays(-7));
            Long visit_people = apiErrorMapper.selectCount(queryWrapper);

            vo = new apiError()
                    .setCount(count)
                    .setDefeatCount(deafCount)
                    //访问量
                    .setVisits(count+deafCount)
                    //访问人次
                    .setVisits_people(visit_people);
            vo.setDateStr(time.plusDays(-7).getDayOfMonth() + "日-" +time.getDayOfMonth()+"日");
            data.add(vo);


            time  = time.plusDays(-7);
        }

        return getPercent(data);

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
            //正确数
            Long count = apiErrorMapper.selectCount(lqw);

            lqw.clear();
            lqw.eq(apiError::getProjectName, projectName)
                    .isNotNull(apiError::getException)
                    .le(apiError::getVisitDate, time)
                    .ge(apiError::getVisitDate, time.plusMonths(-3));
            //访问量
            Long deafCount = apiErrorMapper.selectCount(lqw);
            QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("project_name", projectName)
                    .select("DISTINCT ip")
                    .le("visit_date", time)
                    .ge("visit_date", time.plusMonths(-3));
            Long visit_people = apiErrorMapper.selectCount(queryWrapper);

            vo = new apiError()
                    .setCount(count)
                    .setDefeatCount(deafCount)
                            //访问量
                            .setVisits(count+deafCount)
                                    //访问人次
                                    .setVisits_people(visit_people);
            vo.setDateStr(time.plusMonths(-3).getMonthValue() + "月-" + time.getMonthValue() + "月");
            data.add(vo);
            //visitsSum += count;

            time = time.plusMonths(-3);
        }

        return getPercent(data);
    }

        private List<apiError> getPercent(List<apiError> data) {
        double percent;
        Long PV = 0L;
        Long UV = 0L;
        for (apiError datum : data) {
            //成功率
            percent =   datum.getCount() * 1.000 / datum.getVisits() *100;
            UV += datum.getVisits_people();
            PV += datum.getVisits();
            String  str = String.format("%.2f",percent);
            percent = Double.parseDouble(str);
            datum.setPercent(percent);
        }
            apiError error = new apiError()
                    .setPV(PV)
                    .setUV(UV);
            data.add(error);
            return data;
    }

}
