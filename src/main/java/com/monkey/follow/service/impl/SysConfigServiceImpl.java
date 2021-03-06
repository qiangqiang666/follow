package com.monkey.follow.service.impl;

import com.monkey.follow.constant.SysConfInterface;
import com.monkey.follow.enums.ConfigTypeEnum;
import com.monkey.follow.mapper.ContentMapper;
import com.monkey.follow.mapper.SysConfigMapper;
import com.monkey.follow.model.SysConfig;
import com.monkey.follow.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 描述:
 * 〈〉
 *
 * @author Monkey
 * @create 2020/5/10 21:44
 */
@Slf4j
@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Resource
    private SysConfigMapper sysConfigMapper;
    @Resource
    private ContentMapper contentMapper;

    @PostConstruct
    public void init() {
        log.info("初始化相关配置开始");
        SysConfig sysConfig = sysConfigMapper.selectByPrimaryKey(1);
        SysConfInterface.address = sysConfig.getAddress();
        SysConfInterface.apiKey = sysConfig.getApiKey();
        SysConfInterface.secretKey = sysConfig.getSecretKey();
        SysConfInterface.passphrase = sysConfig.getPassphrase();
        SysConfInterface.hostAddress = sysConfig.getHostAddress();
        SysConfInterface.latestUrl = sysConfig.getLatestUrl();
        SysConfInterface.token = sysConfig.getToken();
        SysConfInterface.typeStr = sysConfig.getTypeStr();
        SysConfInterface.contractClauses = sysConfig.getContractClauses();
        SysConfInterface.multipleNum = sysConfig.getMultipleNum();
        SysConfInterface.minnNum = sysConfig.getMinnNum();
        SysConfInterface.stopLossNum = sysConfig.getStopLossNum();
        SysConfInterface.isStopLoss  = sysConfig.getIsStopLoss();
        SysConfInterface.isFollow = sysConfig.getIsFollow();
        SysConfInterface.stopLossTimes = sysConfig.getStopLossTimes();
        SysConfInterface.scope = sysConfig.getScope();
        SysConfInterface.scopeNum = sysConfig.getScopeNum();
        SysConfInterface.oneselfUserId = sysConfig.getOneselfUserId();
    }

    @Override
    public SysConfig updateInitConf() {
        init();
        return sysConfigMapper.selectByPrimaryKey(1);
    }

    @Override
    public void updateConf(SysConfig sysConfig) {
        sysConfigMapper.updateByPrimaryKeySelective(sysConfig);
    }

    @Override
    public Integer selectContentNum() {
        return contentMapper.selectByTime();
    }

    @Override
    public void clearTime() {
        contentMapper.deleteByTime();
    }


}