package com.monkey.follow.enums;

/**
 * 描述:
 * 〈合约类型〉
 *
 * @author Monkey
 * @create 2020/5/6 22:26
 */
public enum ContractEnum {
    BTC_QUARTER("BTC-USD-", "BTC季度合约"),
    ETH_QUARTER("ETH-USD-", "ETH季度合约"),
    LTC_QUARTER("LTC-USD-", "LTC季度合约"),
    ETC_QUARTER("ETC-USD-", "ETC季度合约"),
    XRP_QUARTER("XRP-USD-", "XRP季度合约"),
    EOS_QUARTER("EOS-USD-", "EOS季度合约"),
    BCH_QUARTER("BCH-USD-", "BCH季度合约"),
    BSV_QUARTER("BSV-USD-", "BSV季度合约"),
    TRX_QUARTER("TRX-USD-", "TRX季度合约"),

    BTC__USDT_QUARTER("BTC-USDT-", "BTC-USDT季度合约"),
    ETH__USDT_QUARTER("ETH-USDT-", "ETH-USDT季度合约"),
    LTC__USDT_QUARTER("LTC-USDT-", "LTC-USDT季度合约"),
    ETC__USDT_QUARTER("ETC-USDT-", "ETC-USDT季度合约"),
    XRP__USDT_QUARTER("XRP-USDT-", "XRP-USDT季度合约"),
    EOS__USDT_QUARTER("EOS-USDT-", "EOS-USDT季度合约"),
    BCH__USDT_QUARTER("BCH-USDT-", "BCH-USDT季度合约"),
    BSV__USDT_QUARTER("BSV-USDT-", "BSV-USDT季度合约"),
    TRX__USDT_QUARTER("TRX-USDT-", "TRX-USDT季度合约"),

    BTC__USD_QUARTER("BTC-USDT-", "BTC-USD-"),
    ETH__USD_QUARTER("ETH-USDT-", "ETH-USD-"),
    LTC__USD_QUARTER("LTC-USDT-", "LTC-USD-"),
    ETC__USD_QUARTER("ETC-USDT-", "ETC-USD-"),
    XRP__USD_QUARTER("XRP-USDT-", "XRP-USD-"),
    EOS__USD_QUARTER("EOS-USDT-", "EOS-USD-"),
    BCH__USD_QUARTER("BCH-USDT-", "BCH-USD-"),
    BSV__USD_QUARTER("BSV-USDT-", "BSV-USD-"),
    TRX__USD_QUARTER("TRX-USDT-", "TRX-USD-"),
    ;

    private String code;

    private String msg;

    public String msg() {
        return msg;
    }

    public String code() {
        return code;
    }

    ContractEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ContractEnum getEnum(String code) {
        for (ContractEnum ele : ContractEnum.values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}