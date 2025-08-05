package com.beraising.agent.omni.core.agents.router.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentBase;
import com.beraising.agent.omni.core.agents.router.IRouterAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.router.IAgentRouterGraph;

@Component
public class RouterAgent extends AgentBase implements IRouterAgent {

    private final IAgentRouterGraph agentRouterGraph;
    private final IAgentRuntimeContextBuilder agentRuntimeContextBuilder;
    private final IAgentStaticContext agentStaticContext;

    public RouterAgent(
            IAgentRouterGraph agentRouterGraph,
            IAgentStaticContext agentStaticContext,
            IAgentRuntimeContextBuilder agentRuntimeContextBuilder) {

        this.agentRouterGraph = agentRouterGraph;
        this.agentStaticContext = agentStaticContext;
        this.agentRuntimeContextBuilder = agentRuntimeContextBuilder;
    }

    @Override
    public String getName() {
        return "Router Agent";
    }

    @Override
    public String getDescription() {
        return "Router Agent用于调度所有Agent";
    }

    @Override
    public IGraph getGraph() {
        return this.agentRouterGraph;
    }

    @Override
    public IAgentStaticContext getAgentStaticContext() {
        return this.agentStaticContext;
    }

    @Override
    public IAgentRuntimeContextBuilder getAgentRuntimeContextBuilder() {
        return this.agentRuntimeContextBuilder;
    }

}
