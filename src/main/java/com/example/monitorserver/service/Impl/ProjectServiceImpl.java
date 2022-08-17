package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.mapper.ProjectMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.utils.MybatisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
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


    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Result getPageProject(int currentPage, int maxMessage,int position) {
        MybatisConfig.setDynamicTableName("t_project");
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
    public Result getAllProject(int position) {
        MybatisConfig.setDynamicTableName("t_project");
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if(position==0){
            queryWrapper.eq("status",1);
        }

        return new Result(ResultEnum.REQUEST_SUCCESS,projectMapper.selectList(queryWrapper));
    }

    @Override
    public String getProjectName(String projectUrl) {
        MybatisConfig.setDynamicTableName("t_project");
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("project_name")
                .eq("project_url",projectUrl);
        Project project = projectMapper.selectOne(queryWrapper);
        return project.getProjectName();
    }

    @Override
    public Result getByCondition(Map<String, Object> map) {
        MybatisConfig.setDynamicTableName("t_project");
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        Iterator<String> iterator = map.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Object value = map.get(key);
            wrapper.like(key,value);
        }
        List<Project> projects = projectMapper.selectList(wrapper);
        return new Result(ResultEnum.SELECT_LIKE,projects);
    }

    @Override
    public Result saveProject(Project project) {

        project.setRegisterDate(LocalDateTime.now());
        projectMapper.insert(project);

        // TODO 2.将权限信息存入
        UserProject userProject = new UserProject()
                .setProjectId(project.getProjectId())
                .setUserId(project.getUserId())
                .setType(1);
        userProjectService.add(userProject);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result updateProject(Project project,int position) {
        MybatisConfig.setDynamicTableName("t_project");
        //普通用户更新项目信息后要重新获得管理员的批准
        if (position==0){
            project.setStatus(0);
            //　TODO 1.查项目名是否重复
            // TODO 1.1 查项目名是否重复
            MybatisConfig.setDynamicTableName("t_project");
            QueryWrapper<Project> wrapper = new QueryWrapper<>();
            wrapper.eq("project_name",project.getProjectName());
            Long count = projectMapper.selectCount(wrapper);
            if (count!=0){
                return new Result(ResultEnum.REQUEST_FALSE);
            }
            // TODO 1.2 查url是否重复
            QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("project_url",project.getProjectUrl());
            Long count1 = projectMapper.selectCount(queryWrapper);
            if (count1!=0){
                return new Result(ResultEnum.REQUEST_FALSE);
            }
        }

        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectUrl,project.getProjectUrl());
        //删除redis首页缓存
        if(Boolean.TRUE.equals(redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()))){
            redisTemplate.delete(RedisEnum.INDEX_KEY.getMsg());
        }
        projectMapper.update(project,wrapper);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result deleteProject(String projectUrl) {
        MybatisConfig.setDynamicTableName("t_project");
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getProjectUrl,projectUrl);
        projectMapper.delete(wrapper);
        return new Result(ResultEnum.DELETE_SUCCESS);
    }

    @Override
    public void scheduleUpdate() {
        //TODO 1.查询用户表中是否存在被冻结用户
        MybatisConfig.setDynamicTableName("t_project");
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", -1);
        List<Project> projects = projectMapper.selectList(queryWrapper);
        if (projects.size() == 0) {
            return;
        }
        // TODO 2.对其冻结日期判断，若已过则更新
        Iterator<Project> iterator = projects.iterator();
        while (iterator.hasNext()) {
            Project project = iterator.next();
            LocalDateTime unsealDate = project.getUnsealDate();
            LocalDateTime dateTime = LocalDateTime.now();
            long now = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long unseal = unsealDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            // TODO 3.当前时间小于解封时间
            if (now > unseal) {
                UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("project_id", project.getProjectId());
                project.setUnsealDate(null);
                project.setStatus(0);
                MybatisConfig.setDynamicTableName("t_project");
                projectMapper.update(project, updateWrapper);
            }
        }
    }
}
