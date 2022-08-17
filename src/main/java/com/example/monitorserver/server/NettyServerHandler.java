package com.example.monitorserver.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//自定义一个handler，需要继承netty，规定好的某个HandlerAdapter(规范)
@Component
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private Option option;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        //将msg转换成一个ByteBuf
        //ByteBuf是netty提供的，不是Nio的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        String str = buf.toString(CharsetUtil.UTF_8);
        //创建表
        Option.MessageHandle(str);
    }


    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush是write+flush
        //将数据写入缓存，并刷新。需要对发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端",CharsetUtil.UTF_8));

    }



    //处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
