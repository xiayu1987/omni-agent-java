package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentRequest;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IGraph {

    void invoke(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext) throws Exception;

    StateGraph getStateGraph() throws Exception;

    IGraphState getGraphState();

    void setGraphState(IGraphState graphState);

    void setAgent(IAgent agent);

    IAgent getAgent();

}
