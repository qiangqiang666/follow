package com.monkey.follow.constant;


import java.math.BigDecimal;

public final class SysConfInterface {

    /**
     * 止损触发次数
     */
    public static Integer stopLossTimes;
    /**
     * 币coin : 主地址
     */
    public static String hostAddress;

    /**
     * 币coin : 最新操作url
     */
    public static String latestUrl;

    /**
     * 币coin : 身份验证的token
     */
    public static String token;

    /**
     * 币coin : 跟单用户的id
     */
    public static String typeStr;

    /**
     * okex : 地址
     */
    public static String address;

    /**
     * okex : ApiKey
     */
    public static String apiKey;

    /**
     * okex : SecretKey
     */
    public static String secretKey;

    /**
     * okex : Passphrase(丢失不可找回)
     */
    public static String passphrase;

    /**
     * okex : 合约期号
     */
    public static String contractClauses;

    /**
     * 自定义参数: 缩小倍数
     */
    public static Integer multipleNum;

    /**
     * 自定义参数: 最小开单张数
     */
    public static String minnNum;

    /**
     * 自定义参数: 余额的止损数值
     */
    public static BigDecimal stopLossNum;

    /**
     * 自定义参数: 止损开关
     */
    public static Boolean isStopLoss;

    /**
     * 自定义参数: 跟单开关
     */
    public static Boolean isFollow;

    /**
     * 自定义参数: 范围保障值
     */
    public static BigDecimal scope;

    /**
     * 币coin : 自身用户的id
     */
    public static String oneselfUserId;

    /**
     * 自定义参数 : 持仓变动预警阀值
     */
    public static Integer scopeNum;

}
