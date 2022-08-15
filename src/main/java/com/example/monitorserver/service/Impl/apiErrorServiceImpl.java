package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.mapper.apiErrorMapper;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.apiError;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.utils.MapBeanUtil;
import com.example.monitorserver.utils.MybatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class apiErrorServiceImpl extends ServiceImpl<apiErrorMapper, apiError> implements apiErrorService {

    @Autowired
    private apiErrorMapper apiErrorMapper;

    @Override
    public void insert(apiError apiError) {
        MybatisConfig.setDynamicTableName("t_apiError");
        apiErrorMapper.insert(apiError);
    }

    @Override
    public Result select(String projectName) {
        MybatisConfig.setDynamicTableName("t_apiError");
        QueryWrapper<apiError> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_name",projectName);
        return new Result(apiErrorMapper.selectOne(queryWrapper));
    }

    @Override
    public void update(apiError apiError) {
        MybatisConfig.setDynamicTableName("t_apiError");
        UpdateWrapper<apiError> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("project_name",apiError.getProjectName());
        apiErrorMapper.update(apiError,updateWrapper);

    }
}
