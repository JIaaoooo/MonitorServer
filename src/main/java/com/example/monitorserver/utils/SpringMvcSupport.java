package com.example.monitorserver.utils;

import com.qgstudio.config.MonitorConfig;
import com.qgstudio.interceptor.MonitorInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan({"com.qgstudio.interceptor","com.qgstudio.config"})
public class SpringMvcSupport extends WebMvcConfigurationSupport {

    @Autowired
    private MonitorInterceptor monitorInterceptor;
    @Autowired
    private MonitorConfig monitorConfig;

    /*@Bean
    public MonitorInterceptor monitorInterceptor(){
        MonitorInterceptor monitorInterceptor1 = new MonitorInterceptor();
        return  monitorInterceptor1;
    }*/

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //配置拦截器,要监控的接口
        registry.addInterceptor(monitorInterceptor).addPathPatterns(monitorConfig.getPathPatterns());
    }
}