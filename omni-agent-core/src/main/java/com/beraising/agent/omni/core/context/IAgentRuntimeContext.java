package com.beraising.agent.omni.core.context;

import com.beraising.agent.omni.core.graph.IGraphState;

public interface IAgentRuntimeContext extends IAgentContext {

    IGraphState getGraphState();

    void setGraphState(IGraphState graphState);

}
