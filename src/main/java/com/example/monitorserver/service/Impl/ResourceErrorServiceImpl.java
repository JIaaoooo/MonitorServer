package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.ResourceErrorMapper;
import com.example.monitorserver.po.Data;
import com.example.monitorserver.po.JsError;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ResourceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: monitor server
 * @description: 资源异常 实现层
 * @author: Jiao
 * @create: 2022-08-14 17：15
 */
@Service
public class ResourceErrorServiceImpl extends ServiceImpl<ResourceErrorMapper, ResourceError> implements ResourceErrorService {

    @Autowired
    private ResourceErrorMapper resourceErrorMapper;

    @Override
    public Result insert(ResourceError resourceError) {
        resourceError.setDate(LocalDateTime.now());
        resourceErrorMapper.insert(resourceError);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result getCount(Data data) {
        LocalDateTime now = LocalDateTime.now();
        int option = data.getOption();
        LocalDateTime dateTime = null;
        switch (option) {
            case 1:
                //获取前半小时的localdatetime格式
                dateTime = now.minusMinutes(30);
                break;
            case 2:
                //获取前一天的
                dateTime = now.minusDays(1);
                break;
            case 3:
                //获取前一个月的时间
                dateTime = now.minusMonths(1);
            default:
                break;
        }
        QueryWrapper<ResourceError> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("date", dateTime);
        Long count = resourceErrorMapper.selectCount(queryWrapper);
        return new Result(ResultEnum.REQUEST_SUCCESS, count);
    }

    @Override
    public Result getFileNameByProject(String projectName) {
        //需要获取tag标签下的所有的路径,但是需要distinct

        //首先需要获取项目存在的错误
        QueryWrapper<ResourceError> qw = new QueryWrapper<>();
        qw.select("distinct tagname").lambda().eq(ResourceError::getProjectName, projectName);
        List<ResourceError> resourceErrors = resourceErrorMapper.selectList(qw);

        List[] array = new List[resourceErrors.size()];


        //获取了每一个的tag,现在需要获取tag下的所有的错误的文件路径,需要distinct
        for (int i = 0; i < resourceErrors.size(); i++) {
            qw = new QueryWrapper<>();
            qw.select("distinct filename,tagname").lambda()
                    .eq(ResourceError::getTagname, resourceErrors.get(i).getTagname())
                    .eq(ResourceError::getProjectName, projectName);
            array[i] = resourceErrorMapper.selectList(qw);
        }

        return new Result(array);
    }

    @Override
    public Result getCountByProject(String projectName) {
        //TODO
        //需要获取tag标签下的所有的路径,但是需要distinct

        //首先需要获取项目存在的错误
        QueryWrapper<ResourceError> qw = new QueryWrapper<>();
        qw.select("distinct tagname").lambda().eq(ResourceError::getProjectName, projectName);
        List<ResourceError> resourceErrors = resourceErrorMapper.selectList(qw);

        qw = new QueryWrapper<>();
        qw.lambda().eq(ResourceError::getProjectName,projectName);
        Long sum = 0L;
        sum = resourceErrorMapper.selectCount(qw);
        Long count = 0L;
        List<ResourceError> listVo = new LinkedList<>();

        ResourceError vo = null;
        for (ResourceError resourceError : resourceErrors) {
            qw = new QueryWrapper<>();
            qw.lambda().eq(ResourceError::getTagname, resourceError.getTagname())
                    .eq(ResourceError::getProjectName, projectName);
            count = resourceErrorMapper.selectCount(qw);

            vo = new ResourceError();
            vo.setCount(count).setPercent(getDouble(count * 100.0 / sum)).setTagname(resourceError.getTagname());

            listVo.add(vo);
        }
        return new Result(listVo);

    }
    private double getDouble(double percent) {
        String  str = String.format("%.2f",percent);
        percent = Double.parseDouble(str);
        return percent;
    }


}
