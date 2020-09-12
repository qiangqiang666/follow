package com.monkey.follow.service;

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
}
