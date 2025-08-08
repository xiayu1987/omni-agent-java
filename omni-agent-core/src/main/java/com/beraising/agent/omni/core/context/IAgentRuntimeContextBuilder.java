package com.beraising.agent.omni.core.context;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;

public interface IAgentRuntimeContextBuilder {

    IAgentRuntimeContext build(IAgentEvent agentEvent, IAgentGraph graph) throws Exception;

}
