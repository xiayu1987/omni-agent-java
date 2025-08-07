package com.beraising.agent.omni.core.graph;

import java.util.List;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public interface IGraph {

    void invoke(IAgentRuntimeContext agentRuntimeContext, IEventListener eventListener) throws Exception;

    StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception;

    void setAgent(IAgent agent);

    IAgent getAgent();

    void setGraphNodes(List<IGraphNode> graphNodes);

    List<IGraphNode> getGraphNodes();

    IGraphState newGraphState();

}
