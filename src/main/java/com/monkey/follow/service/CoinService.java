package com.monkey.follow.service;

import com.monkey.follow.model.jsoup.AccountInfo;

import java.math.BigDecimal;

public interface CoinService {

    /**
     * 跟单
     *
     * @param typeStr 跟单用户id
     */
    void getLatestOperation(String typeStr);

    /**
     * 止损
     *
     * @param accountType 账户类型
     * @param currency 币本位
     */
    void stopLoss(String accountType, String currency);

    /**
     * 检测遗留仓位
     * @param typeStr
     */
    void legacy(String typeStr);

    /**
     * 跟单操作: 策略为: 根据币coin中金主的倍数和张数来进行跟随
     * @param typeStr 跟单用户id
     * @param oneselfUserId 自身用户id
     * @param scope 保障范围值
     * @param scope 持仓变动次数的阀门值
     */
    void positionSendOrder(String typeStr, String oneselfUserId, BigDecimal scope,Integer scopeNum);

    /**
     * 根据请求,拿到数据结果集,二次处理数据
     * @param typeStr 跟单用户id
     * @return
     */
    AccountInfo getPositionData(String typeStr);
}
