package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.IAgentResponse;

public class AgentResponse implements IAgentResponse {

    @Override
    public IAgentResponse copy() {

        return new AgentResponse();
    }

}
