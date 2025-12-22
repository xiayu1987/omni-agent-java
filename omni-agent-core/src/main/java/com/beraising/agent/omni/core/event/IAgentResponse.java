package com.beraising.agent.omni.core.event;

/**
 * IAgentResponse接口定义了代理响应的基本操作方法
 * 该接口提供了获取和设置响应类型、响应数据以及复制响应对象的功能
 */
public interface IAgentResponse {

    /**
     * 获取代理响应的类型
     *
     * @return 返回代理响应的类型枚举值
     */
    EAgentResponseType getResponseType();

    /**
     * 设置代理响应的类型
     *
     * @param responseType 要设置的代理响应类型枚举值
     */
    void setResponseType(EAgentResponseType responseType);

    /**
     * 获取代理响应的数据内容
     *
     * @return 返回代理响应的数据字符串
     */
    String getResponseData();

    /**
     * 设置代理响应的数据内容
     *
     * @param responseData 要设置的代理响应数据字符串
     */
    void setResponseData(String responseData);

    /**
     * 创建当前代理响应对象的副本
     *
     * @return 返回一个新的代理响应对象，包含与当前对象相同的数据
     */
    IAgentResponse copy();

}

