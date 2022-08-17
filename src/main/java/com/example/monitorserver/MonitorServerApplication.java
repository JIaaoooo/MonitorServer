package com.example.monitorserver;

import com.example.monitorserver.server.NettyServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MonitorServerApplication {



    public static void main(String[] args) throws Exception {

//        //TODO API接口文档
//
//        DocsConfig config = new DocsConfig();
//
//        // 项目根目录
//        config.setProjectPath("E:\\github_project\\qg_final_exam\\MonitorServer");
//        // 项目名称
//        config.setProjectName("MonitorServer");
//        // 声明该API的版本
//        config.setApiVersion("V1.4.4");
//        // 生成API 文档所在目录
//        config.setDocsPath("E:\\api_docs\\MonitorServer");
//        // 配置自动生成
//        config.setAutoGenerate(Boolean.TRUE);
//        config.addPlugin(new MarkdownDocPlugin());
//        // 执行生成文档
//        Docs.buildHtmlDocs(config);

        SpringApplication.run(MonitorServerApplication.class, args);
        new NettyServer().run();

    }

}
