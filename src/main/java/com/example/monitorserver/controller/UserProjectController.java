package com.example.monitorserver.controller;


import com.example.monitorserver.po.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monitor server
 * @description: 用户与项目关系
 * @author: Jiao
 * @create: 2022-08-09 18:36
 */
@RestController
@Slf4j
@RequestMapping(value = "/userproject",produces = "application/json;charset=UTF-8")
public class UserProjectController {

    /**
     * 分页查看有权限的用户，管理员权限
     * @return  返回用户List
     */
    public Result viewPermission(){
        return null;
    }
}
