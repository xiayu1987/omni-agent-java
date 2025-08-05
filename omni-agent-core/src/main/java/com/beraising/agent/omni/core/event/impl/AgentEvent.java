package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.session.IAgentSession;

import lombok.Builder;

@Builder
public class AgentEvent implements IAgentEvent {

    private IAgentRequest agentRequest;
    private IAgentResponse agentResponse;
    private IAgentSession agentSession;

    public IAgentSession getAgentSession() {
        return agentSession;
    }

    public void setAgentSession(IAgentSession agentSession) {
        this.agentSession = agentSession;
    }

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

}
