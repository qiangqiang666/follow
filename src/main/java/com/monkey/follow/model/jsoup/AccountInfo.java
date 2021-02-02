package com.monkey.follow.model.jsoup;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 描述:
 * 〈用户合约数据〉
 *
 * @author Monkey
 * @create 2021/1/29 16:22
 */
@Data
public class AccountInfo {

    /**
     * 账户昵称
     */
    private String nickName;

    /**
     * 总余额 单位usdt
     */
    private BigDecimal balance;

    /**
     * 多比
     */
    private BigDecimal futureLongAmount;

    /**
     * 多比的具体金额
     */
    private BigDecimal longBalance;

    /**
     * 空比
     */
    private BigDecimal futureShortAmount;

    /**
     * 空比的具体金额
     */
    private BigDecimal shortBalance;

    /**
     * 持仓数据
     */
    private List<Position> list;

    /**
     * 持仓空单总金额
     */
    private BigDecimal shortQtyTotal;

    /**
     * 持仓多单总金额
     */
    private BigDecimal longQtyTotal;
}