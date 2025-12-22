package com.beraising.agent.omni.core.session;

import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;

/**
 * IAgentSessionItem接口定义了代理会话项的基本操作方法。
 * 该接口提供了对代理名称、请求和响应对象的访问和设置功能。
 */
public interface IAgentSessionItem {

    /**
     * 获取代理名称
     *
     * @return 返回当前代理的名称字符串
     */
    String getAgentName();

    /**
     * 设置代理名称
     *
     * @param agentName 代理名称字符串
     */
    void setAgentName(String agentName);

    /**
     * 获取代理请求对象
     *
     * @return 返回IAgentRequest类型的代理请求对象
     */
    IAgentRequest getAgentRequest();

    /**
     * 设置代理请求对象
     *
     * @param agentRequest IAgentRequest类型的代理请求对象
     */
    void setAgentRequest(IAgentRequest agentRequest);

    /**
     * 获取代理响应对象
     *
     * @return 返回IAgentResponse类型的代理响应对象
     */
    IAgentResponse getAgentResponse();

    /**
     * 设置代理响应对象
     *
     * @param agentResponse IAgentResponse类型的代理响应对象
     */
    void setAgentResponse(IAgentResponse agentResponse);

}
