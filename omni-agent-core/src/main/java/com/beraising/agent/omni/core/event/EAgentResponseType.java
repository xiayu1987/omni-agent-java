package com.beraising.agent.omni.core.event;

public enum EAgentResponseType {

    TEXT(0, "TEXT"),
    FORM(1, "FORM"),
    PICTURE(2, "PICTURE"),
    MARKDOWN(3, "MARKDOWN");

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
