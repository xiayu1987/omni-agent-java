package com.beraising.agent.omni.agents.router.graph.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.agents.router.graph.IAgentRouterGraph;
import com.beraising.agent.omni.agents.router.graph.nodes.RouteNode;
import com.beraising.agent.omni.agents.router.graph.state.RouterState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.AgentGraphBase;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

@Component
public class AgentRouterGraph extends AgentGraphBase<RouterState> implements IAgentRouterGraph {

    private static final String ROUTER_NODE_NAME = "router_node";

    public AgentRouterGraph() {
        super();
    }

    @Override
    public StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception {

        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode(ROUTER_NODE_NAME, AsyncNodeAction.node_async(new RouteNode(ROUTER_NODE_NAME, this)))

                .addEdge(StateGraph.START, ROUTER_NODE_NAME)
                .addEdge(ROUTER_NODE_NAME, StateGraph.END);

        return stateGraph;
    }

    @Override
    public IGraphState newGraphState() {
        return new RouterState();
    }

    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            RouterState graphState) {
        graphState.putUserInput(input, agentRuntimeContext, agentEvent);
    }

    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, RouterState graphState) {

    }

    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, RouterState graphState) {

        if (graphNode == null) {
            return AgentResponse.builder().responseType(EAgentResponseType.TEXT)
                    .responseData(graphState.getRouterResult()).build();
        }

        return AgentResponse.builder().build();
    }

}
