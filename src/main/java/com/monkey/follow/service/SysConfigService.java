package com.monkey.follow.service;


import com.monkey.follow.model.SysConfig;

public interface SysConfigService {

    /**
     * 手动更新配置
     */
    SysConfig updateInitConf();

    /**
     * 修改配置
     */
    void updateConf(SysConfig sysConfig);
}
