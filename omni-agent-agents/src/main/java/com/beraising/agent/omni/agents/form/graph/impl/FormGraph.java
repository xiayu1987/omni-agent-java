package com.beraising.agent.omni.agents.form.graph.impl;

import java.util.Map;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.agents.form.graph.IFormGraph;
import com.beraising.agent.omni.agents.form.graph.nodes.FormGetNode;
import com.beraising.agent.omni.agents.form.graph.nodes.FormSubmitNode;
import com.beraising.agent.omni.agents.form.graph.state.FormState;
import com.beraising.agent.omni.agents.form.graph.state.FormSubmitData;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.AgentGraphBase;
import com.beraising.agent.omni.core.graph.edge.impl.ConditionalEdge;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.node.impl.InterruptNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.google.gson.Gson;

@Component
public class FormGraph extends AgentGraphBase<FormState> implements IFormGraph {

    private static final String FORM_GET_NODE_NAME = "form_get_node";
    private static final String FORM_SUBMIT_NODE_NAME = "form_submit_node";
    private static final String FORM_SUBMIT_INTERRUPT_NODE_NAME = "form_submit_interrupt_node";
    private static final String FORM_GET_INTERRUPT_NODE_NAME = "form_get_interrupt_node";
    private static final String FORM_SUBMIT_CONDITIONAL_EDGE_NAME = "form_submit_conditional_edge";

    private ToolCallbackProvider formTools;
    private Resource formGetFormat;
    private Resource formSubmitFormat;

    public FormGraph(ToolCallbackProvider formTools,
            @Value("classpath:agents-prompts/form/form-get-format.txt") Resource formGetFormat,
            @Value("classpath:agents-prompts/form/form-submit-format.txt") Resource formSubmitFormat) {
        super();
        this.formTools = formTools;
        this.formGetFormat = formGetFormat;
        this.formSubmitFormat = formSubmitFormat;
    }

    @Override
    public StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception {
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode(FORM_GET_NODE_NAME,
                        AsyncNodeAction.node_async(
                                new FormGetNode(FORM_GET_NODE_NAME, this, formTools, formGetFormat)))
                .addNode(FORM_SUBMIT_NODE_NAME,
                        AsyncNodeAction.node_async(
                                new FormSubmitNode(FORM_SUBMIT_NODE_NAME, this, formTools, formSubmitFormat)))
                .addNode(FORM_SUBMIT_INTERRUPT_NODE_NAME,
                        AsyncNodeAction.node_async(
                                new InterruptNode<FormState>(FORM_SUBMIT_INTERRUPT_NODE_NAME, this)))
                .addNode(FORM_GET_INTERRUPT_NODE_NAME,
                        AsyncNodeAction.node_async(
                                new InterruptNode<FormState>(FORM_GET_INTERRUPT_NODE_NAME, this)))

                .addEdge(StateGraph.START, FORM_GET_NODE_NAME)
                .addEdge(FORM_GET_NODE_NAME, FORM_GET_INTERRUPT_NODE_NAME)
                .addEdge(FORM_GET_INTERRUPT_NODE_NAME, FORM_SUBMIT_NODE_NAME)
                .addConditionalEdges(FORM_SUBMIT_NODE_NAME,
                        AsyncEdgeAction
                                .edge_async((new ConditionalEdge<FormState>(
                                        FORM_SUBMIT_CONDITIONAL_EDGE_NAME, this,
                                        (graphState, agentRuntimeContext,
                                                agentEvent) -> {
                                            if (graphState.getFormSubmitResult()
                                                    .getResultType() == FormSubmitData.SUCCESS) {
                                                return StateGraph.END;
                                            }
                                            if (graphState.getFormSubmitResult()
                                                    .getResultType() == FormSubmitData.SYSTEMERROR) {
                                                return StateGraph.END;
                                            }
                                            if (graphState.getFormSubmitResult()
                                                    .getResultType() == FormSubmitData.BIZERROR) {
                                                return FORM_SUBMIT_INTERRUPT_NODE_NAME;
                                            }
                                            return StateGraph.END;
                                        }))),
                        Map.of(
                                FORM_SUBMIT_INTERRUPT_NODE_NAME,
                                FORM_SUBMIT_INTERRUPT_NODE_NAME,
                                StateGraph.END,
                                StateGraph.END))
                .addEdge(FORM_SUBMIT_INTERRUPT_NODE_NAME, FORM_SUBMIT_NODE_NAME);

        return stateGraph;
    }

    @Override
    public IGraphState newGraphState() {
        return new FormState();
    }

    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent,
            FormState graphState) {

        graphState.putUserInput(input, agentRuntimeContext, agentEvent);
    }

    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, FormState graphState) {
        if (graphNode.getName().equals(FORM_SUBMIT_NODE_NAME)) {
            graphState.putFormDataFeedback(feedBack, agentRuntimeContext, agentEvent);
        }
    }

    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, FormState graphState) {

        if (graphNode == null) {
            return AgentResponse.builder().responseType(EAgentResponseType.FORM)
                    .responseData(new Gson().toJson(graphState.getFormSubmitResult())).build();
        }

        if (graphNode.getName().equals(FORM_GET_INTERRUPT_NODE_NAME)) {
            return AgentResponse.builder().responseType(EAgentResponseType.FORM)
                    .responseData(graphState.getFormGetResult()).build();
        }

        if (graphNode.getName().equals(FORM_SUBMIT_INTERRUPT_NODE_NAME)) {
            return AgentResponse.builder().responseType(EAgentResponseType.FORM)
                    .responseData(new Gson().toJson(graphState.getFormSubmitResult())).build();
        }

        return AgentResponse.builder().build();
    }

}