package com.example.monitorserver.utils;

import com.example.monitorserver.interceptor.LoginInterceptor;
import com.qgstudio.config.MonitorConfig;
import com.qgstudio.interceptor.MonitorInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan({"com.qgstudio"})
public class SpringMvcSupport extends WebMvcConfigurationSupport {

    @Autowired
    private MonitorInterceptor monitorInterceptor;
    @Autowired
    private MonitorConfig monitorConfig;


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //配置拦截器,要监控的接口
        registry.addInterceptor(monitorInterceptor).addPathPatterns(monitorConfig.getPathPatterns());
        registry.addInterceptor(new LoginInterceptor(redisTemplate))
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/SDK",
                "/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/*.html", "/**/*.html","/swagger-resources/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(
                "classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
}