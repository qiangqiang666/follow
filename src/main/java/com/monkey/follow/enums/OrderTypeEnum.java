package com.monkey.follow.enums;

/**
 * 描述:
 * 〈下单类型〉
 *
 * @author Monkey
 * @create 2020/5/6 22:26
 */
public enum OrderTypeEnum {

    COMMON("0", "普通委托（order type不填或填0都是普通委托）"),
    PO("1", "只做Maker（Post only）"),
    FOK("2", "全部成交或立即取消（FOK） "),
    IOC("3", "立即成交并取消剩余（IOC）"),
    MARKET("4", "市价委托"),
    ;

    private String code;

    private String msg;

    public String msg() {
        return msg;
    }

    public String code() {
        return code;
    }

    OrderTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static OrderTypeEnum getEnum(String code) {
        for (OrderTypeEnum ele : OrderTypeEnum.values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
