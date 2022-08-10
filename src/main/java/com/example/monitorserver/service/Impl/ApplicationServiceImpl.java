package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.Mapper.ApplicationMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: monitor server
 * @description: 申请服务实现层
 * @author: Jiao
 * @create: 2022-08-09 17:17
 * @version: 1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {


    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ProjectService projectService;

    @Override
    public Result releaseApp(Application application) {
        applicationMapper.insert(application);
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result updateApp(Application application) {

        //TODO 1.更新Application表中的数据
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId,application.getApplicationId());
        applicationMapper.update(application,wrapper);

        //TODO 2.  删除
        int type  = application.getType();
        if(type==3){
            if(application.getStatus()==0){
                //删除项目
                projectService.deleteProject(application.getProjectId());
            }
        }
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result selectApp(Long applicationId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId,applicationId);
        Application selectOne = applicationMapper.selectOne(wrapper);
        return new Result(ResultEnum.REQUEST_SUCCESS, selectOne);
    }


}
