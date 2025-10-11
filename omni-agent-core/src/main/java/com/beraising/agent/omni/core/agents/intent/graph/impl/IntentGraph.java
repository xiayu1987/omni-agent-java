package com.beraising.agent.omni.core.agents.intent.graph.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.core.agents.intent.graph.IIntentGraph;
import com.beraising.agent.omni.core.agents.intent.graph.nodes.IntentNode;
import com.beraising.agent.omni.core.agents.intent.graph.state.IntentState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.AgentGraphBase;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

@Component
public class IntentGraph extends AgentGraphBase<IntentState> implements IIntentGraph {

    private static final String ROUTER_NODE_NAME = "intent_node";
    private final Resource intentFormat;

    public IntentGraph(@Value("classpath:agents-prompts/intent/intent-format.txt") Resource intentFormat) {
        super();
        this.intentFormat = intentFormat;
    }

    @Override
    public StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception {

        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode(ROUTER_NODE_NAME,
                        AsyncNodeAction.node_async(new IntentNode(ROUTER_NODE_NAME, this, intentFormat)))

                .addEdge(StateGraph.START, ROUTER_NODE_NAME)
                .addEdge(ROUTER_NODE_NAME, StateGraph.END);

        return stateGraph;
    }

    @Override
    public IGraphState newGraphState() {
        return new IntentState();
    }

    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IntentState graphState) {
        graphState.putUserInput(input, agentRuntimeContext, agentEvent);
    }

    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, IntentState graphState) {

    }

    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, IntentState graphState) {

        if (graphNode == null) {
            return AgentResponse.builder().responseType(EAgentResponseType.TEXT)
                    .responseData(graphState.getIntentResult()).build();
        }

        return AgentResponse.builder().build();
    }

}
