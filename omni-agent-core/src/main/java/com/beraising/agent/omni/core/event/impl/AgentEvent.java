package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.ISseChanel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * AgentEvent 类表示代理事件，实现了 IAgentEvent 接口。
 * 该类包含了代理请求、响应以及相关的会话信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvent implements IAgentEvent {

    @JsonDeserialize(as = AgentRequest.class)
    private IAgentRequest agentRequest;
    @JsonDeserialize(as = AgentResponse.class)
    private IAgentResponse agentResponse;
    private String agentSessionId;
    private String parentAgentSessionId;
    @Builder.Default
    private EUserType userType = EUserType.USER;
    private String userId;
    private boolean isStream;
    @JsonIgnore
    private transient ISseChanel sseChanel;

    /**
     * 创建当前 AgentEvent 对象的副本。
     * 该方法会深度复制 agentRequest 和 agentResponse 字段，其他字段进行浅复制。
     *
     * @return 返回一个新的 AgentEvent 对象，它是当前对象的副本
     */
    @Override
    public IAgentEvent copy() {
        AgentEvent copy = new AgentEvent();
        // 深度复制 agentRequest，如果原对象不为 null 则调用其 copy 方法
        copy.setAgentRequest(agentRequest != null ? agentRequest.copy() : null);
        // 深度复制 agentResponse，如果原对象不为 null 则调用其 copy 方法
        copy.setAgentResponse(agentResponse != null ? agentResponse.copy() : null);
        // 复制基本类型和引用类型字段
        copy.setAgentSessionId(agentSessionId);
        copy.setParentAgentSessionId(parentAgentSessionId);
        copy.setUserId(userId);
        copy.setStream(isStream);
        copy.setSseChanel(sseChanel);
        copy.setUserType(userType);
        return copy;
    }


}
