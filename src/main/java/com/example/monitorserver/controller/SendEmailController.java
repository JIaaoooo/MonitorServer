package com.example.monitorserver.controller;

import com.example.monitorserver.annotation.Secret;
import com.example.monitorserver.po.Mail;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.User;
import com.example.monitorserver.service.SendEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Struct;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-11 16:08
 **/

@RestController
@RequestMapping(value = "/mail", produces = "application/json;charset=UTF-8")
@Slf4j
@CrossOrigin("http://localhost:3000")
@Validated
@Api(tags = "mail接口")
public class SendEmailController {
    @Resource
    private SendEmailService sendEmailService;

    @PostMapping
    @ApiOperation("发送报警邮件")
    @Secret
    public Result sendEmail(@ApiParam(name="subject,text,projectName",value="邮件标题，邮件内容,项目名称",required = true )@RequestBody Mail mail) {

        log.debug("发邮件");

        return new Result(sendEmailService.sendSimpleEmail(mail.getSubject(), mail.getText(), mail.getProjectName()));
    }
}
