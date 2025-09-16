package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.ISseChanel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvent implements IAgentEvent {

    private IAgentRequest agentRequest;
    private IAgentResponse agentResponse;
    private String agentSessionId;
    private String userId;
    private boolean isStream;
    @JsonIgnore
    private transient ISseChanel sseChanel;

    @Override
    public IAgentEvent copy() {
        AgentEvent copy = new AgentEvent();
        copy.setAgentRequest(agentRequest != null ? agentRequest.copy() : null);
        copy.setAgentResponse(agentResponse != null ? agentResponse.copy() : null);
        copy.setAgentSessionId(agentSessionId);
        copy.setUserId(userId);
        copy.setStream(isStream);
        copy.setSseChanel(sseChanel);
        return copy;
    }

}
