package com.monkey.follow.enums;

public enum ConfigTypeEnum {

    pro(0, "正式环境"),
    test(1, "测试环境")
    ;

    private Integer code;

    private String msg;

    public String msg() {
        return msg;
    }

    public Integer code() {
        return code;
    }

    ConfigTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ConfigTypeEnum getEnum(Integer code) {
        for (ConfigTypeEnum ele : ConfigTypeEnum.values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
