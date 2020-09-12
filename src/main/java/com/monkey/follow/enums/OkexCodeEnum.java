package com.monkey.follow.enums;

public enum OkexCodeEnum {
    code_32014("平仓张数大于可平", "32014 : Positions that you are closing exceeded the total qty of contracts allowed to close"),
    code_32015("代表可开张数不足,也就是意思该合约下的余额不足", "32015 : Risk rate lower than 100% before opening position"),
    code_30014("在规定时间内,请求次数频繁", "30014 : Too Many Requests"),
    ;

    private String code;

    private String msg;

    public String msg() {
        return msg;
    }

    public String code() {
        return code;
    }

    OkexCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static OkexCodeEnum getEnum(String code) {
        for (OkexCodeEnum ele : OkexCodeEnum.values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
