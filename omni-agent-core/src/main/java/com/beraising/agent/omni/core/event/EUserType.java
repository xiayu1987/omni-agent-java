package com.beraising.agent.omni.core.event;

public enum EUserType {

    USER(0, "用户"),
    SYSTEM(1, "系统");

    private final int code;
    private final String message;

    EUserType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static EUserType fromCode(int code) {
        for (EUserType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type code: " + code);
    }

}
