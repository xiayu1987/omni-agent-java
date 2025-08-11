package com.beraising.agent.omni.agents.form.graph.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.agents.form.graph.IFormGraph;
import com.beraising.agent.omni.agents.form.graph.nodes.FormBuildNode;
import com.beraising.agent.omni.agents.form.graph.state.FormState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.AgentGraphBase;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

@Component
public class FormGraph extends AgentGraphBase<FormState> implements IFormGraph {
    private static final String FORM_BUILD_NODE_NAME = "form_build_node";
    private static final String FORM_PARAM_INIT_NODE_NAME = "form_param_init_node";
    private static final String FORM_CHOOSE_NODE_NAME = "form_choose_node";

    @Override
    public StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception {
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode(FORM_CHOOSE_NODE_NAME,
                        AsyncNodeAction.node_async(new FormBuildNode(FORM_CHOOSE_NODE_NAME, this)))
                .addNode(FORM_CHOOSE_NODE_NAME,
                        AsyncNodeAction.node_async(new FormBuildNode(FORM_CHOOSE_NODE_NAME, this)))
                .addNode(FORM_CHOOSE_NODE_NAME,
                        AsyncNodeAction.node_async(new FormBuildNode(FORM_CHOOSE_NODE_NAME, this)))

                .addEdge(StateGraph.START, FORM_PARAM_INIT_NODE_NAME)
                .addEdge(FORM_PARAM_INIT_NODE_NAME, StateGraph.END);

        return stateGraph;
    }

    @Override
    public IGraphState newGraphState() {
        return new FormState();
    }

    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            FormState graphState) {

        graphState.putUserInput(input, agentRuntimeContext, agentEvent);
    }

    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, FormState graphState) {

    }

    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, FormState graphState) {
        return AgentResponse.builder().build();
    }

}