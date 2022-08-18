package com.example.monitorserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.BlankError;
import org.springframework.stereotype.Service;

/**
 * @program: monitor server
 * @description: 白屏异常服务层
 * @author: Jiao
 * @create: 2022-08-14 14：07
 */
@Service
public interface BlankErrorService extends IService<BlankError> {

    Result insert(BlankError blankError);

    /**
     * 获取白屏次数
     * @param projectName 通过项目名查询
     * @return 返回白屏次数
     */
    Long getBlankCount(String projectName);

    Result getJsErrByType(String projectName,Integer type);
}
