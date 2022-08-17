package com.example.monitorserver.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Message;
import com.example.monitorserver.po.Result;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: 申请信息 服务处
 * @author: Jiao
 * @create: 2022-08-09 17:17
 */
@Service
public interface MessageService extends IService<Message> {

    /**
     * 通过当前登录用户的Id，获取他的消息,并将其user表中message消息重置
     * @param userId 用户Id
     * @return 返回申请信息，给予判断是否同意
     */
    Result getApplication(String userId);

    /**
     * 将用户ID与申请ID绑定
     * @param message 信息对象
     * @return 返回操作结果
     */
    Result addMessage(Message message);

    /**
     * 更新消息状态
     * @param message 消息
     */
    void update(Message message);
}
