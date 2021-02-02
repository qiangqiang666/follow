package com.monkey.follow.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 描述:
 * 〈配置类〉
 *
 * @author Monkey
 * @create 2020/5/10 21:27
 */
@Data
@Table(name = "t_config")
public class SysConfig {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 币coin : 主地址
     */
    private String hostAddress;

    /**
     * 币coin : 最新操作url
     */
    private String latestUrl;

    /**
     * 币coin : 身份验证的token
     */
    private String token;

    /**
     * 币coin : 跟单用户的id
     */
    private String typeStr;

    /**
     * 币coin : 自身用户的id
     */
    private String oneselfUserId;

    /**
     * okex : 地址
     */
    private String address;

    /**
     * okex : ApiKey
     */
    private String apiKey;

    /**
     * okex : SecretKey
     */
    private String secretKey;

    /**
     * okex : Passphrase(丢失不可找回)
     */
    private String passphrase;

    /**
     * okex : 合约期号
     */
    private String contractClauses;

    /**
     * 自定义参数: 缩小倍数
     */
    private Integer multipleNum;

    /**
     * 自定义参数: 最小开单张数
     */
    private String minnNum;

    /**
     * 自定义参数: 余额的止损数值
     */
    private BigDecimal stopLossNum;

    /**
     * 自定义参数: 止损开关
     */
    private Boolean isStopLoss;

    /**
     * 自定义参数: 止损触发次数
     */
    private Integer stopLossTimes;

    /**
     * 自定义参数: 跟单开关
     */
    private Boolean isFollow;

    /**
     * 自定义参数: 范围保障值
     */
    private BigDecimal scope;

    /**
     * 自定义参数 : 持仓变动预警阀值
     */
    public Integer scopeNum;
}