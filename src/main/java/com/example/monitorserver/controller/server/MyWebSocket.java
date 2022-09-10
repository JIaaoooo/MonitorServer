package com.example.monitorserver.controller.server;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.monitorserver.config.WebSocketConfig;
import com.example.monitorserver.po.User;
import com.example.monitorserver.utils.GlobalWsMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 20:11
 **/
@ServerEndpoint(prefix = "netty-websocket")
@Component
@Slf4j
public class MyWebSocket{



    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, ParameterMap parameterMap) throws IOException {

        //TODO 新建连接是需要判断是否有权限,里面有一个token,需要jiao来完成


        //权限通过,连接成功
        System.out.println("new connection");

        //获取用户id
//        String userId = parameterMap.getParameter("userId");

        //获取项目名称
        String projectName = parameterMap.getParameter("projectName");


        //维护map集合
        GlobalWsMap.onLine(projectName,session);

       log.debug("当前在线人数为: {}", GlobalWsMap.WS_BY_USER_ID_AND_PRO_ID_MAP.size());
       log.debug("监控的项目为: {}",projectName);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        //关闭连接
        System.out.println("one connection closed");

        GlobalWsMap.leave(session);

        log.debug("当前在线人数为: {}", GlobalWsMap.WS_BY_USER_ID_AND_PRO_ID_MAP.size());

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(message);


        session.sendText("傻逼 cd");
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }
}

