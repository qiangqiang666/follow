package com.monkey.follow.enums;

/**
 * 描述:
 * 〈全平方向〉
 *
 * @author Monkey
 * @create 2020/5/6 22:26
 */
public enum AllFlatTypeEnum {
    FLAT_MORE("long", "平多"),
    FLAT_NULL("short", "平空")
    ;

    private String code;

    private String msg;

    public String msg() {
        return msg;
    }

    public String code() {
        return code;
    }

    AllFlatTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static AllFlatTypeEnum getEnum(String code) {
        for (AllFlatTypeEnum ele : AllFlatTypeEnum.values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
