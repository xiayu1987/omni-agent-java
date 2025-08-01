package com.beraising.agent.omni.core.graph.router.impl;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.core.graph.GraphBase;
import com.beraising.agent.omni.core.graph.router.IAgentRouterGraph;
import com.beraising.agent.omni.core.graph.router.nodes.RouteNode;
import com.beraising.agent.omni.core.graph.router.state.RouterState;

@Component
public class AgentRouterGraph extends GraphBase<RouterState> implements IAgentRouterGraph {

    private static final String ROUTER_NODE_NAME = "router_node";

    @Override
    public StateGraph getStateGraph() throws Exception {

        StateGraph stateGraph = new StateGraph(getGraphState().getKeyStrategyFactory())
                .addNode(ROUTER_NODE_NAME, AsyncNodeAction.node_async(new RouteNode(this)))

                .addEdge(StateGraph.START, ROUTER_NODE_NAME)
                .addEdge(ROUTER_NODE_NAME, StateGraph.END);

        return stateGraph;
    }

    @Override
    public RouterState newGraphState() {
        return new RouterState();
    }

}
