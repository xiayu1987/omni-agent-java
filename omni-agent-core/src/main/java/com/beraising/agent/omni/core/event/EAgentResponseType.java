package com.beraising.agent.omni.core.event;

public enum EAgentResponseType {
    ERROR(0, "ERROR"),
    TEXT(1, "TEXT"),
    FORM(2, "FORM"),
    PICTURE(3, "PICTURE"),
    MARKDOWN(4, "MARKDOWN");

    private final int code;
    private final String message;

    EAgentResponseType(int code, String message) {
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
