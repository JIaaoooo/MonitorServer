package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.Mapper.MessageMapper;
import com.example.monitorserver.constant.ResultEnum;
import com.example.monitorserver.po.Message;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.service.ApplicationService;
import com.example.monitorserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @program: monitor server
 * @description: 申请信息 服务实现层
 * @author: Jiao
 * @create: 2022-08-09 17:17
 */

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {


    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public Result getApplication(Long userId) {

        //TODO 1.查询该用户下的申请信息ID
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId,userId)
                .select(Message::getApplicationId);
        List<Message> messages = messageMapper.selectList(wrapper);
        //TODO 2.通过applicationId去调用Application获取申请信息,用一个Map集合存储，后返回
        Map<String,Object> map = new HashMap<>();
        int count =0;
        for (Message message : messages) {
            count++;
            long applicationId = message.getApplicationId();
            Result result = applicationService.selectApp(applicationId);
            Object data = result.getData();
            map.put("Application"+count,data);
        }

        return new Result(ResultEnum.APPLICATION_MESSAGE, map);
    }
}
