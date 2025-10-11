package com.beraising.agent.omni.core.context;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;

public interface IAgentRuntimeContextBuilder {

    IAgentRuntimeContext initialize(IAgentEvent agentEvent) throws Exception;

    IAgentRuntimeContext enrich(IAgentRuntimeContext context, IAgentGraph graph) throws Exception;

    IAgentRuntimeContext build(IAgentEvent agentEvent, IAgentGraph graph) throws Exception;

}
