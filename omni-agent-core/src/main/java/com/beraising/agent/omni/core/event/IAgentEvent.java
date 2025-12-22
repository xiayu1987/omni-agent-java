package com.beraising.agent.omni.core.event;

public interface IAgentEvent {

    /**
     * 获取代理请求对象
     *
     * @return 代理请求对象
     */
    IAgentRequest getAgentRequest();

    /**
     * 设置代理请求对象
     *
     * @param agentRequest 代理请求对象
     */
    void setAgentRequest(IAgentRequest agentRequest);

    /**
     * 获取代理响应对象
     *
     * @return 代理响应对象
     */
    IAgentResponse getAgentResponse();

    /**
     * 设置代理响应对象
     *
     * @param agentResponse 代理响应对象
     */
    void setAgentResponse(IAgentResponse agentResponse);

    /**
     * 获取代理会话ID
     *
     * @return 代理会话ID
     */
    String getAgentSessionId();

    /**
     * 设置代理会话ID
     *
     * @param agentSessionId 代理会话ID
     */
    void setAgentSessionId(String agentSessionId);

    /**
     * 获取父级代理会话ID
     *
     * @return 父级代理会话ID
     */
    String getParentAgentSessionId();

    /**
     * 设置父级代理会话ID
     *
     * @param parentAgentSessionId 父级代理会话ID
     */
    void setParentAgentSessionId(String parentAgentSessionId);

    /**
     * 获取用户类型
     *
     * @return 用户类型枚举值
     */
    EUserType getUserType();

    /**
     * 设置用户类型
     *
     * @param userType 用户类型枚举值
     */
    void setUserType(EUserType userType);

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    String getUserId();

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    void setUserId(String userId);

    /**
     * 判断是否为流式处理
     *
     * @return true表示流式处理，false表示非流式处理
     */
    boolean isStream();

    /**
     * 设置是否为流式处理
     *
     * @param stream true表示流式处理，false表示非流式处理
     */
    void setStream(boolean stream);

    /**
     * 获取SSE通道
     *
     * @return SSE通道对象
     */
    ISseChanel getSseChanel();

    /**
     * 设置SSE通道
     *
     * @param sseChanel SSE通道对象
     */
    void setSseChanel(ISseChanel sseChanel);

    /**
     * 复制当前事件对象
     *
     * @return 复制后的事件对象
     */
    IAgentEvent copy();

}

