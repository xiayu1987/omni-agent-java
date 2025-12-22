package com.beraising.agent.omni.core.event;

/**
 * IAgentRequest接口定义了代理请求的基本操作规范
 * 该接口提供了对请求类型、请求数据的访问和修改方法，以及对象复制功能
 */
public interface IAgentRequest {

    /**
     * 获取请求类型
     * @return 返回当前请求的类型枚举值
     */
    EAgentRequestType getRequestType();

    /**
     * 设置请求类型
     * @param requestType 要设置的请求类型枚举值
     */
    void setRequestType(EAgentRequestType requestType);

    /**
     * 获取请求数据
     * @return 返回请求数据字符串
     */
    String getRequestData();

    /**
     * 设置请求数据
     * @param requestData 要设置的请求数据字符串
     */
    void setRequestData(String requestData);

    /**
     * 复制当前请求对象
     * @return 返回当前请求对象的一个副本
     */
    IAgentRequest copy();

}

