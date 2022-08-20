package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.*;
import com.example.monitorserver.service.*;
import com.example.monitorserver.service.apiErrorService;
import com.example.monitorserver.mapper.ProjectMapper;
import com.example.monitorserver.utils.NettyEventGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    private ApplicationService applicationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private apiErrorService apiErrorService;

    @Override
    public Result getPageProject(int currentPage, int maxMessage, int position) {
        Page<Project> page = new Page<>(currentPage, maxMessage);
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
    public Result getAllProject(int position) throws ExecutionException, InterruptedException {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if(position==0){
            queryWrapper.eq("status",1);
        }
        List<Project> projects = projectMapper.selectList(queryWrapper);
        List<Project> projectList = new ArrayList<>();
        Iterator<Project> iterator = projects.iterator();
        while (iterator.hasNext()){
            Project project = iterator.next();
            Result whole = apiErrorService.getWhole(project.getProjectName());
            apiError apiError = (com.example.monitorserver.po.apiError) whole.getData();
            project.setPV(apiError.getPV());
            project.setUV(apiError.getUV());
            project.setRate(apiError.getRate());
            projectList.add(project);
            if (project.getStatus()==-1){
                //判断是否已解冻
                LocalDateTime unsealDate = project.getUnsealDate();
                LocalDateTime dateTime = LocalDateTime.now();
                long now = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long unseal = unsealDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if (now>unseal){
                    project.setUnsealDate(null);
                    project.setStatus(1);
                    updateProject(project,9);
                }
            }
        }
        Result result = new Result(ResultEnum.REQUEST_SUCCESS, projectList);
        List<Project> projects1 = (List<Project>) result.getData();
        redisTemplate.opsForList().leftPush(RedisEnum.INDEX_KEY.getMsg(), projects1);
        return new Result(ResultEnum.REQUEST_SUCCESS,projects1);
    }

    @Override
    public String getProjectName(String projectUrl) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("project_name")
                .eq("project_url",projectUrl);
        Project project = projectMapper.selectOne(queryWrapper);
        return project.getProjectName();
    }

    @Override
    public Result getByCondition(Map<String, Object> map) {
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
        NioEventLoopGroup group = NettyEventGroup.group;
        //同名判断
        QueryWrapper<Project> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("project_name",project.getProjectName());
        Long count = projectMapper.selectCount(queryWrapper);
        if (count!=0){
            return new Result(ResultEnum.REGISTER_NAME_DOUBLE);
        }
        queryWrapper.clear();
        queryWrapper.eq("project_url",project.getProjectUrl());
        count = projectMapper.selectCount(queryWrapper);
        if (count != 0){
            return new Result(ResultEnum.REGISTER_NAME_DOUBLE);
        }
        group.next().submit(()->{
            projectMapper.insert(project);
        });

        group.next().submit(()->{
            // TODO 2.将权限信息存入
            UserProject userProject = new UserProject()
                    .setProjectId(project.getProjectId())
                    .setUserId(project.getUserId())
                    .setType(1);
            userProjectService.add(userProject);
        });
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result updateProject(Project project,int position) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = NettyEventGroup.group;
        //普通用户更新项目信息后要重新获得管理员的批准
        if (position==0){
            project.setStatus(0);
            //　TODO 1.查项目名是否重复
            // TODO 1.1 查项目名是否重复

            Future<Long> count1 = group.next().submit(() -> {
                QueryWrapper<Project> wrapper = new QueryWrapper<>();
                wrapper.eq("project_name", project.getProjectName());
                Long count = projectMapper.selectCount(wrapper);
                return count;
            });
            if (count1.get()!=0){
                return new Result(ResultEnum.REQUEST_FALSE);
            }


            // TODO 1.2 查url是否重复
            Future<Long> count2 = group.next().submit(() -> {
                QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_url", project.getProjectUrl());
                Long c = projectMapper.selectCount(queryWrapper);
                return c;
            });
            if (count2.get()!=0){
                return new Result(ResultEnum.REQUEST_FALSE);
            }
        }

        group.next().submit(()->{
            UpdateWrapper<UserProject> queryWrapper = new UpdateWrapper<>();
            queryWrapper.eq("project_id",project.getProjectId());
            UserProject userProject = new UserProject()
                    .setProjectId(project.getProjectId())
                    .setUserId(project.getUserId())
                    .setType(1)
                    .setStatus(project.getStatus());
            userProjectService.update(userProject,queryWrapper);
        });

        group.next().submit(()->{
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Project::getProjectUrl,project.getProjectUrl());
            projectMapper.update(project,wrapper);
            //删除redis首页缓存
            if(Boolean.TRUE.equals(redisTemplate.hasKey(RedisEnum.INDEX_KEY.getMsg()))){
                redisTemplate.delete(RedisEnum.INDEX_KEY.getMsg());
            }
        });

        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result deleteProject(Data data) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = NettyEventGroup.group;

        Future<Project> projectFuture = group.next().submit(() -> {
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Project::getProjectName, data.getProjectName());
            // TODO 获取项目id ，删除userproject、application相关信息（message相关信息）
            return projectMapper.selectOne(wrapper);
        });

        group.next().submit(()->{
            UserProject userProject = null;
            try {
                userProject = new UserProject()
                            .setUserId(data.getUserId())
                            .setProjectId(projectFuture.get().getProjectId());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            userProjectService.delete(userProject);
        });


        Future<Application> application = group.next().submit(() -> {
            Map<String, Object> condition = new HashMap<>();
            condition.put("project_id", projectFuture.get().getProjectId());
            Result result = applicationService.selectApp(condition);
            List<Application> applications = (List<Application>) result.getData();
            return applications.iterator().next();
        });


        //申请表中相关信息
        group.next().submit(()->{
            Map<String,Object> deleteMap = new HashMap<>();
            try {
                deleteMap.put("project_id",projectFuture.get().getProjectId());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            applicationService.deleteAppli(deleteMap);
        });

        //删除消息表中相关信息
        group.next().submit(()->{
            Map<String,Object> condition2 = new HashMap<>();
            try {
                condition2.put("application_id",application.get().getApplicationId());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            messageService.delete(condition2);
        });


        //删除project表中信息
        group.next().submit(()->{
            LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
            try {
                wrapper.eq(Project::getProjectName, projectFuture.get().getProjectName());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            projectMapper.delete(wrapper);
        });

        return new Result(ResultEnum.DELETE_SUCCESS);

    }

}
