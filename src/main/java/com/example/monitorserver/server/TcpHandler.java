package com.example.monitorserver.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: monitor server
 * @description: TCP服务器处理方法
 * @author: Jiao
 * @create: 2022-08-10  16:50
 */
@Component
@Slf4j
public  class TcpHandler extends SimpleChannelInboundHandler<String> {

    private static Map<String,Channel> channels=null;


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String channelID = ctx.channel().id().asLongText();
        log.debug(channelID+"加入连接");
        channels.put("连接"+channelID,ctx.channel());
    }



    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(ctx.channel().id().asLongText()+"断开连接");
    }

    /**
     * 发来消息
     * @param ctx channel上下文
     * @param s 消息内容
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }



}
