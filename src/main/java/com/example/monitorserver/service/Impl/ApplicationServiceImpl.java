package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.mapper.ApplicationMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Application;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.ProjectService;
import com.example.monitorserver.service.UserProjectService;
import com.example.monitorserver.utils.DynamicTableNameConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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


    static {
        DynamicTableNameConfig.setDynamicTableName("application");
    }
    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserProjectService userProjectService;

    @Override
    public Result releaseApp(Application application) {

        // TODO 1.将申请信息存入
        applicationMapper.insert(application);
        // TODO 2.信息推送 当类型为请求监控、删除项目时都需要向项目发布者发送消息通知也做同意
        String projectId = application.getProjectId();
        HashMap<String, Object> map = new HashMap<>();
        // TODO 2.1查询userProject表，获取项目的发布者  条件：项目id type 为1
        map.put("project_id",projectId);
        map.put("type",1);
        Result select = userProjectService.select(map);
        List<UserProject> data = (List<UserProject>) select.getData();

        // TODO 3.获得对应的发布者信息
        Iterator<UserProject> iterator = data.iterator();
        while (iterator.hasNext()){
            // TODO 3.1获得项目发布者的ID
            String userId = iterator.next().getUserId();
            // TODO 4.将用户ID和申请信息ID绑定

        }
        return new Result(ResultEnum.REQUEST_SUCCESS);
    }

    @Override
    public Result updateApp(Application application) {

        //TODO 1.更新Application表中的数据
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId,application.getApplicantId());
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
    public Result selectApp(String applicationId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId,applicationId);
        Application selectOne = applicationMapper.selectOne(wrapper);
        return new Result(selectOne);
    }


}
