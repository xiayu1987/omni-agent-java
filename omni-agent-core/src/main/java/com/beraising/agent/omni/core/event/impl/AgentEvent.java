package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvent implements IAgentEvent {

    private IAgentRequest agentRequest;
    private IAgentResponse agentResponse;
    private String agentSessionID;
    private String parentAgentSessionID;

    public IAgentRequest getAgentRequest() {
        return agentRequest;
    }

    public void setAgentRequest(IAgentRequest agentRequest) {
        this.agentRequest = agentRequest;
    }

    public IAgentResponse getAgentResponse() {
        return agentResponse;
    }

    public void setAgentResponse(IAgentResponse agentResponse) {
        this.agentResponse = agentResponse;
    }

    @Override
    public String getAgentSessionID() {
        return agentSessionID;
    }

    @Override
    public void setAgentSessionID(String agentSessionID) {
        this.agentSessionID = agentSessionID;
    }

    @Override
    public String getParentAgentSessionID() {
        return parentAgentSessionID;
    }

    @Override
    public void setParentAgentSessionID(String parentAgentSessionID) {
        this.parentAgentSessionID = parentAgentSessionID;

    }

    @Override
    public IAgentEvent copy() {
        AgentEvent copy = new AgentEvent();
        copy.setAgentRequest(agentRequest != null ? agentRequest.copy() : null);
        copy.setAgentResponse(agentResponse != null ? agentResponse.copy() : null);
        copy.setAgentSessionID(agentSessionID);
        copy.setParentAgentSessionID(parentAgentSessionID);
        return copy;
    }

}
