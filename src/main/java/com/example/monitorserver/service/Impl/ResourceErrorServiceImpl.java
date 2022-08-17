package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.mapper.ResourceErrorMapper;
import com.example.monitorserver.po.ResourceError;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ResourceErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    public Long getResourceCount(String projectName) {
        QueryWrapper<ResourceError> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        Long count = resourceErrorMapper.selectCount(queryWrapper);
        return count;
    }


}
