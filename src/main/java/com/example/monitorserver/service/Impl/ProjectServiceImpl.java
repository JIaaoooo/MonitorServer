package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.mapper.ProjectMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.utils.DynamicTableNameConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @program: monitor server
 * @description: 项目服务实现层
 * @author: Jiao
 * @create: 2022-08-09 12：30
 * @version: 1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper,Project> implements ProjectService {

    static {
        DynamicTableNameConfig.setDynamicTableName("project");
    }
    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public Result getPageProject(int currentPage, int maxMessage,int position) {
        Page<Project> page = new Page(currentPage, maxMessage);
        if (position!=0){
            //超级管理员，没有项目状态限制
            page = projectMapper.selectPage(page, null);
        }
        else{
            //普通用户
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Project::getStatus,0);
            page = projectMapper.selectPage(page,wrapper);
        }
        List<Project> records = page.getRecords();
        return new Result(ResultEnum.SELECT_PAGE,records);
    }

    @Override
    public Result getByCondition(Map<String, Object> map) {

        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        String key = map.keySet().iterator().next();
        log.debug(key);
        wrapper.like(key,map.get(key));
        List<Project> projects = projectMapper.selectList(wrapper);
        return new Result(ResultEnum.SELECT_LIKE,projects);
    }

    @Override
    public Result saveProject(Project project) {
        projectMapper.insert(project);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result updateProject(Project project) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectId,project.getProjectId());
        projectMapper.update(project,wrapper);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result deleteProject(String projectId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectId,projectId);
        projectMapper.delete(wrapper);
        return new Result(ResultEnum.DELETE_SUCCESS);
    }


}
