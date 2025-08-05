package com.beraising.agent.omni.core.context.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IGraphState;

public class AgentRuntimeContext implements IAgentRuntimeContext {

    private IGraphState graphState;
    private IAgentEvent agentEvent;

    public IGraphState getGraphState() {
        return graphState;
    }

    public void setGraphState(IGraphState graphState) {
        this.graphState = graphState;
    }

    @Override
    public IAgentEvent getAgentEvent() {
        return agentEvent;
    }

    @Override
    public void setAgentEvent(IAgentEvent agentEvent) {
        this.agentEvent = agentEvent;
    }

}
