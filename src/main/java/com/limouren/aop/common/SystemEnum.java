package com.limouren.aop.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SystemEnum {
    SYSTEM_FRONT("front", "用户"),
    SYSTEM_SELF("self", "本系统"),
    SYSTEM_OTHER("other", "外系统");

    private String code, desc;

    SystemEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, SystemEnum> map = new HashMap<>();

    static {
        for (SystemEnum e : EnumSet.allOf(SystemEnum.class)) {
            map.put(e.code, e);
        }
    }

    public static String getDesc(String code) {
        SystemEnum e = map.get(code);
        return e == null ? "" : e.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
