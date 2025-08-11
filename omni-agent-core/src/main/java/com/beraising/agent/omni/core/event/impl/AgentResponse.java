package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentResponse;

public class AgentResponse implements IAgentResponse {

    private EAgentRequestType requestType;

    public EAgentRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(EAgentRequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public IAgentResponse copy() {

        return new AgentResponse();
    }

}
