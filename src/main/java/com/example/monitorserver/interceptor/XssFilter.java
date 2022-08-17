package com.example.monitorserver.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: aop_annotation
 * @description:
 * @author: stop.yc
 * @create: 2022-08-10 14:57
 **/
@Component
public  class XssFilter implements Filter{
    Logger log  = LoggerFactory.getLogger(this.getClass());

    // 忽略权限检查的url地址
    private final String[] excludeUrls = new String[]{
            "null"
    };

    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
           throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        //获取请求url的后两层
        String url = req.getServletPath() + pathInfo;
        //获取请求你ip后的全部路径
        String uri = req.getRequestURI();
        //注入xss过滤器实例
        XssHttpServletRequestWrapper reqW = new XssHttpServletRequestWrapper(req);
        log.debug("用了过滤器");
        //过滤掉不需要的Xss校验的地址
        for (String str : excludeUrls) {
            if (uri.indexOf(str) >= 0) {
                arg2.doFilter(arg0, response);
                return;
            }
        }
        //过滤
        arg2.doFilter(reqW, response);
    }
    public void destroy() {
    }
    public void init(FilterConfig filterconfig1) throws ServletException {
    }
}
