package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.UserProject;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: 用户与项目关系
 * @author: Jiao
 * @create: 2022-08-09 18:37
 */
@Service
public interface UserProjectService extends IService<UserProject> {

    Result add(UserProject userProject);
}
