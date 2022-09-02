package com.example.monitorserver.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.monitorserver.mapper.CacheMapper;
import com.example.monitorserver.po.Cache;
import com.example.monitorserver.service.CacheService;

public class CacheServiceImpl extends ServiceImpl<CacheMapper, Cache> implements CacheService {

    @Override
    public Cache getTotal() {
        return null;
    }
}
