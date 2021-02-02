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

    /**
     * 持仓实时变动次数
     * @return
     */
    Integer selectContentNum();

    /**
     * 清楚持仓变动测试
     */
    void clearTime();

}
