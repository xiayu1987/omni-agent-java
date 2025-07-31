package com.beraising.agent.omni.core.context.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.graph.IGraphState;

@Component
public class AgentRuntimeContext implements IAgentRuntimeContext {

    private IGraphState graphState;

    public IGraphState getGraphState() {
        return graphState;
    }

    public void setGraphState(IGraphState graphState) {
        this.graphState = graphState;
    }

}
