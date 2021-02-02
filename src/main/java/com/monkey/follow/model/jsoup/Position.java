package com.monkey.follow.model.jsoup;

import lombok.Data;

/**
 * 描述:
 * 〈持仓数据〉
 *
 * @author Monkey
 * @create 2021/1/29 16:19
 */
@Data
public class Position {

    /**
     * 张数
     */
    private Integer qty;

    /**
     * 类型: long 多  short 空
     */
    private String type;

    /**
     * 币种名: 例如 BTC
     */
    private String valueUnit;

    /**
     * 交易所名称: 例如 Okex
     */
    private String exChange;
}