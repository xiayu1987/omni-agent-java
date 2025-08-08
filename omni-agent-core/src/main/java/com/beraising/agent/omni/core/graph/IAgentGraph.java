package com.beraising.agent.omni.core.graph;

import java.util.List;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public interface IAgentGraph {

    void init(IAgent agent, IEventListener eventListener,
            IAgentGraphListener agentGraphListener) throws Exception;

    IAgentEvent invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception;

    StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception;

    void setAgent(IAgent agent);

    IAgent getAgent();

    void setAgentGraphListener(IAgentGraphListener agentGraphListener);

    IAgentGraphListener getAgentGraphListener();

    void setEventListener(IEventListener eventListener);

    IEventListener getEventListener();

    void setGraphNodes(List<IGraphNode> graphNodes);

    List<IGraphNode> getGraphNodes();

    IGraphState newGraphState();

}
