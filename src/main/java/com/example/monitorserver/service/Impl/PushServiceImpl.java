package com.example.monitorserver.service.Impl;

import com.example.monitorserver.config.WebSocketConfig;
import com.example.monitorserver.service.PushService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 20:13
 **/
@Service
public class PushServiceImpl implements PushService {
    @Override
    public void pushMsgToOne(String userId, String msg){
//        ConcurrentHashMap<String, Channel> userChannelMap = WebSocketConfig.getUserChannelMap();
//        Channel channel = userChannelMap.get(userId);
//        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }
    @Override
    public void pushMsgToAll(String msg){
//        WebSocketConfig.getChannelGroup().writeAndFlush(new TextWebSocketFrame(msg));
    }

}
