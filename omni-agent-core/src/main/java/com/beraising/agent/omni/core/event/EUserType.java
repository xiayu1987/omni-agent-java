package com.beraising.agent.omni.core.event;
/**
 * 用户类型枚举类
 * 定义了系统中的用户类型及其对应的编码和描述信息
 */
public enum EUserType {

    USER(0, "用户"),
    SYSTEM(1, "系统");

    private final int code;
    private final String message;

    /**
     * 构造函数
     * @param code 类型编码
     * @param message 类型描述信息
     */
    EUserType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取类型编码
     * @return 类型编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取类型描述信息
     * @return 类型描述信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 根据编码获取对应的用户类型枚举值
     * @param code 用户类型编码
     * @return 对应的用户类型枚举值
     * @throws IllegalArgumentException 当传入的编码不匹配任何已定义的用户类型时抛出此异常
     */
    public static EUserType fromCode(int code) {
        // 遍历所有枚举值，查找编码匹配的用户类型
        for (EUserType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type code: " + code);
    }

}

