package com.beraising.agent.omni.core.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
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

    default Map<String, Object> createInput(IAgentRuntimeContext agentRuntimeContext) {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(IGraphState.getAgentSessionIDKey(), agentRuntimeContext.getAgentSessionID());
        putInput(inputMap, agentRuntimeContext, ListUtils.lastOf(agentRuntimeContext.getAgentEvents()));
        return inputMap;
    }

    default Map<String, Object> createFeedBack(IAgentRuntimeContext agentRuntimeContext, IGraphNode graphNode) {
        Map<String, Object> inputMap = new HashMap<>();
        putFeedBack(inputMap, agentRuntimeContext, ListUtils.lastOf(agentRuntimeContext.getAgentEvents()), graphNode);
        return inputMap;
    }

    IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode);

    void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent);

    void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode);

}
