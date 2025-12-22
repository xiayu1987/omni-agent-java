package com.beraising.agent.omni.core.event;

/**
 * EAgentRequestType枚举类定义了代理请求的类型
 * 包含文本类型和数据类型两种请求类型
 */
public enum EAgentRequestType {

    TEXT(0, "文本"),
    DATA(1, "数据");

    private final int code;
    private final String message;

    /**
     * 构造函数，初始化代理请求类型的编码和描述信息
     * @param code 请求类型的编码值
     * @param message 请求类型的描述信息
     */
    EAgentRequestType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取请求类型的编码值
     * @return 返回对应的整数编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取请求类型的描述信息
     * @return 返回对应的字符串描述
     */
    public String getMessage() {
        return message;
    }

}
