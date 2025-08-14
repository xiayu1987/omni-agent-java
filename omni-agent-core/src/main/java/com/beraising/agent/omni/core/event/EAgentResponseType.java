package com.beraising.agent.omni.core.event;

public enum EAgentResponseType {
    ERROR(0, "错误"),
    TEXT(1, "文本"),
    FORM(2, "表单"),
    PICTURE(3, "图片"),
    MARKDOWN(4, "Markdown");

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
