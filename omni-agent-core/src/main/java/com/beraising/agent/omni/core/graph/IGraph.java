package com.beraising.agent.omni.core.graph;


import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IGraph {

    void invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception;

    StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception;

    void setAgent(IAgent agent);

    IAgent getAgent();

    IGraphState newGraphState();

}
