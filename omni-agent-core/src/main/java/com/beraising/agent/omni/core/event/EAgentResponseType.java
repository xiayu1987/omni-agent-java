package com.beraising.agent.omni.core.event;
/**
 * EAgentResponseType枚举类定义了代理响应的类型
 * 包含各种响应类型的编码和描述信息
 */
public enum EAgentResponseType {
    ERROR(0, "错误"),
    TEXT(1, "文本"),
    FORM(2, "表单"),
    PICTURE(3, "图片"),
    MARKDOWN(4, "Markdown");

    private final int code;
    private final String message;

    /**
     * 构造函数，初始化响应类型的编码和消息
     * @param code 响应类型编码
     * @param message 响应类型描述信息
     */
    EAgentResponseType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取响应类型编码
     * @return 响应类型编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取响应类型描述信息
     * @return 响应类型描述信息
     */
    public String getMessage() {
        return message;
    }

}

