package com.beraising.agent.omni.core.context.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.IAgentEvent;

public class AgentRuntimeContextBuilder implements IAgentRuntimeContextBuilder {

    @Override
    public IAgentRuntimeContext build(IAgentEvent agentEvent) {

        AgentRuntimeContext result = new AgentRuntimeContext();
        result.setAgentEvent(agentEvent);

        return result;
    }

}
