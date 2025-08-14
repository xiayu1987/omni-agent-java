package com.beraising.agent.omni.mcp.service.form;

public enum EFormType {

    LEAVE(0, "请假"),
    BUSINESS_TRIP(1, "公出"),
    TEST_ERROR(2, "测试错误");

    private final int code;
    private final String message;

    EFormType(int code, String message) {
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
