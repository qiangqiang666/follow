package com.monkey.follow.enums;

/**
 * 描述:
 * 〈开单方向〉
 *
 * @author Monkey
 * @create 2020/5/6 22:26
 */
public enum BizTypeEnum {

    sell_empty("2", "卖出开空"),
    sell_more("3", "卖出平多"),
    buy_empty("4", "买入平空"),
    buy_more("1", "买入开多"),

    ;

    private String code;

    private String msg;

    public String msg() {
        return msg;
    }

    public String code() {
        return code;
    }

    BizTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BizTypeEnum getEnum(String code) {
        for (BizTypeEnum ele : BizTypeEnum.values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
