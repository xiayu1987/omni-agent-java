package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;

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
    private String agentSessionID;

    @Override
    public IAgentEvent copy() {
        AgentEvent copy = new AgentEvent();
        copy.setAgentRequest(agentRequest != null ? agentRequest.copy() : null);
        copy.setAgentResponse(agentResponse != null ? agentResponse.copy() : null);
        copy.setAgentSessionID(agentSessionID);
        return copy;
    }

}
