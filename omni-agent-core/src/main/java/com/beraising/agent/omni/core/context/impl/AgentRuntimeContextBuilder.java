package com.beraising.agent.omni.core.context.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.IInterruptNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

@Component
public class AgentRuntimeContextBuilder implements IAgentRuntimeContextBuilder {

    public AgentRuntimeContextBuilder() {
    }

    @Override
    public IAgentRuntimeContext build(IAgentEvent agentEvent, IAgentGraph graph)
            throws Exception {
        if (agentEvent == null) {
            throw new IllegalArgumentException("Agent event cannot be null");
        }
        if (agentEvent.getAgentSessionId() == null) {
            throw new IllegalArgumentException("Agent session ID cannot be null");
        }

        IAgentRuntimeContext agentRuntimeContext = new AgentRuntimeContext();
        agentRuntimeContext.setAgentRuntimeContextId(UUID.randomUUID().toString());
        agentRuntimeContext.setAgentSessionId(agentEvent.getAgentSessionId());
        agentRuntimeContext.setAgent(graph.getAgent());
        agentRuntimeContext.setIsEnd(false);

        IGraphState graphState = graph.newGraphState();
        agentRuntimeContext.setGraphState(graphState);

        HashMap<String, KeyStrategy> stateKeys = graphState.getStateKeys();

        KeyStrategyFactory keyStrategyFactory = () -> {

            HashMap<String, KeyStrategy> result = new HashMap<>();
            result.putAll(IGraphState.getDefaultStateKeys());
            result.putAll(stateKeys);
            return result;
        };

        graph.setGraphNodes(new ArrayList<>());
        graph.setGraphEdges(new ArrayList<>());
        StateGraph stateGraph = graph.getStateGraph(keyStrategyFactory);

        String[] interruptNodes = graph.getGraphNodes().stream()
                .filter(node -> node instanceof IInterruptNode)
                .map(node -> node.getName())
                .toArray(String[]::new);

        CompiledGraph compiledGraph = stateGraph
                .compile(CompileConfig.builder()
                        .saverConfig(graph.getAgent().getAgentStaticContext().getGraphSaverConfig())
                        .withLifecycleListener(graph.getAgentGraphListener().getStateGraphLifecycleListener())
                        .interruptAfter(interruptNodes)
                        .build());
        agentRuntimeContext.setCompiledGraph(compiledGraph);

        agentRuntimeContext.getAgentEvents().add(agentEvent);

        return agentRuntimeContext;
    }

}
