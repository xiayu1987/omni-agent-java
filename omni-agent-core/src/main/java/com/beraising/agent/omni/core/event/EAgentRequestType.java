package com.beraising.agent.omni.core.event;

public enum EAgentRequestType {

    TEXT(0, "文本"),
    DATA(1, "数据");

    private final int code;
    private final String message;

    EAgentRequestType(int code, String message) {
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
