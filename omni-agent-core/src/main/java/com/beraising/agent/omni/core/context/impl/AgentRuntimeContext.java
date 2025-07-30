package com.beraising.agent.omni.core.context.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.graph.IGraphState;

public class AgentRuntimeContext implements IAgentRuntimeContext {

    private IGraphState graphState;

    public IGraphState getGraphState() {
        return graphState;
    }

    public void setGraphState(IGraphState graphState) {
        this.graphState = graphState;
    }

}
