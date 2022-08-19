package com.example.monitorserver.utils;


import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyEventGroup {

    public  static NioEventLoopGroup group = new NioEventLoopGroup(10);
}
