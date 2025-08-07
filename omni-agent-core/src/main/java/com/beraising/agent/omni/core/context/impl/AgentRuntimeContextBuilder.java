package com.beraising.agent.omni.core.context.impl;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

@Component
public class AgentRuntimeContextBuilder implements IAgentRuntimeContextBuilder {

    private final IAgentSessionManage agentSessionManage;

    public AgentRuntimeContextBuilder(IAgentSessionManage agentSessionManage) {
        this.agentSessionManage = agentSessionManage;
    }

    @Override
    public IAgentRuntimeContext build(IAgent agent, IAgentEvent agentEvent) throws Exception {
        if (agentEvent == null) {
            throw new IllegalArgumentException("Agent event cannot be null");
        }

        if (agentEvent.getAgentSessionID() == null) {
            throw new IllegalArgumentException("Agent session ID cannot be null");
        }
        IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

        if (agentSession == null) {
            throw new IllegalArgumentException("Agent session cannot be null");
        }

        IAgentRuntimeContext agentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());

        if (agentRuntimeContext == null || agentRuntimeContext.isEnd()) {
            agentRuntimeContext = new AgentRuntimeContext();
            agentRuntimeContext.setAgentSessionID(agentEvent.getAgentSessionID());
            agentRuntimeContext.setAgent(agent);

        } else {
            agentRuntimeContext.setIsEnd(false);
        }

        IGraphState graphState = agentRuntimeContext.getGraphState();
        CompiledGraph compiledGraph = agentRuntimeContext.getCompiledGraph();

        if (graphState == null) {
            graphState = agent.getGraph().newGraphState();
            agentRuntimeContext.setGraphState(graphState);
        }

        if (compiledGraph == null) {

            HashMap<String, KeyStrategy> stateKeys = graphState.getStateKeys();

            KeyStrategyFactory keyStrategyFactory = () -> {

                HashMap<String, KeyStrategy> result = new HashMap<>();
                result.putAll(IGraphState.getDefaultStateKeys());
                result.putAll(stateKeys);
                return result;
            };

            StateGraph stateGraph = agent.getGraph().getStateGraph(keyStrategyFactory);

            compiledGraph = stateGraph
                    .compile(CompileConfig.builder()
                            .saverConfig(agent.getAgentStaticContext().getGraphSaverConfig())
                            .build());
            agentRuntimeContext.setCompiledGraph(compiledGraph);
        }

        agentRuntimeContext.getAgentEvents().add(agentEvent);

        return agentRuntimeContext;
    }

}
