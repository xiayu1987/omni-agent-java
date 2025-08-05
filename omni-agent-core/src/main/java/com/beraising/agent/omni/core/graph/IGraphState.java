package com.beraising.agent.omni.core.graph;

import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IGraphState {

    OverAllState getState();

    void setState(OverAllState state);

    Map<String, Object> createInput(IAgentRuntimeContext agentRuntimeContext);

}
