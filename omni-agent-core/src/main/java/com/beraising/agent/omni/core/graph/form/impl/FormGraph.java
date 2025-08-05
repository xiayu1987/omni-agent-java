package com.beraising.agent.omni.core.graph.form.impl;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.graph.GraphBase;
import com.beraising.agent.omni.core.graph.IGraphStateBuilder;
import com.beraising.agent.omni.core.graph.form.IFormGraph;
import com.beraising.agent.omni.core.graph.form.nodes.FormBuildNode;
import com.beraising.agent.omni.core.graph.form.state.FormState;

@Component
public class FormGraph extends GraphBase<FormState> implements IFormGraph {
    private static final String FORM_NODE_NAME = "form_node";

    @Override
    public StateGraph getStateGraph() throws Exception {
        StateGraph stateGraph = new StateGraph(getGraphStateBuilder().getKeyStrategyFactory())
                .addNode(FORM_NODE_NAME, AsyncNodeAction.node_async(new FormBuildNode(this)))

                .addEdge(StateGraph.START, FORM_NODE_NAME)
                .addEdge(FORM_NODE_NAME, StateGraph.END);

        return stateGraph;
    }

    @Override
    public IGraphStateBuilder<FormState> getGraphStateBuilder() {
        return new IGraphStateBuilder<FormState>() {

            @Override
            public FormState build(IAgentRuntimeContext agentRuntimeContext) {
                return new FormState();
            }

            @Override
            public KeyStrategyFactory getKeyStrategyFactory() {
                return FormState.getKeyStrategyFactory();
            }

        };
    }
}