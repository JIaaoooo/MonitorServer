package com.example.monitorserver.service;


import com.example.monitorserver.po.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public interface ManagerService {

    /**
     * 分页获取传入的实例化对象信息
     * @param currentPage 当前页
     * @param maxMessage  每页最多展示的数量
     * @return
     */
    <T> Result<T> getAllUser(int currentPage,int maxMessage,T bean);


}
