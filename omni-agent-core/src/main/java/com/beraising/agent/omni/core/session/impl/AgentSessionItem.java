package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentRequest;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AgentSessionItem类用于表示代理会话项，实现了IAgentSessionItem接口。
 * 该类包含了代理请求、代理响应和代理名称等信息，用于管理单个代理的会话数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItem implements IAgentSessionItem {

    /**
     * 代理请求对象，使用AgentRequest类进行反序列化
     */
    @JsonDeserialize(as = AgentRequest.class)
    private IAgentRequest agentRequest;

    /**
     * 代理响应对象，使用AgentResponse类进行反序列化
     */
    @JsonDeserialize(as = AgentResponse.class)
    private IAgentResponse agentResponse;

    /**
     * 代理名称
     */
    private String agentName;

}
