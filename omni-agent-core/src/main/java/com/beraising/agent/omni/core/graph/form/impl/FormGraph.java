package com.beraising.agent.omni.core.graph.form.impl;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.core.graph.GraphBase;
import com.beraising.agent.omni.core.graph.form.IFormGraph;
import com.beraising.agent.omni.core.graph.form.nodes.FormBuildNode;
import com.beraising.agent.omni.core.graph.router.state.RouterState;

@Component
public class FormGraph extends GraphBase implements IFormGraph {
    private static final String FORM_NODE_NAME = "form_node";

    @Override
    public StateGraph getStateGraph() throws Exception {
        StateGraph stateGraph = new StateGraph(RouterState.getKeyStrategyFactory())
                .addNode(FORM_NODE_NAME, AsyncNodeAction.node_async(new FormBuildNode(this)))

                .addEdge(StateGraph.START, FORM_NODE_NAME)
                .addEdge(FORM_NODE_NAME, StateGraph.END);

        return stateGraph;
    }
}