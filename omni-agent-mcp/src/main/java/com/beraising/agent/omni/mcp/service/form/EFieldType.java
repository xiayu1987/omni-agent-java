package com.beraising.agent.omni.mcp.service.form;

public enum EFieldType {

    TEXT(0, "文本"),
    NUMBER(1, "数字"),
    TIME(2, "时间");

    private final int code;
    private final String message;

    EFieldType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
