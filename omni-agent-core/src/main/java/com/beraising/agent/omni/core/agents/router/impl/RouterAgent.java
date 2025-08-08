package com.beraising.agent.omni.core.agents.router.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentBase;
import com.beraising.agent.omni.core.agents.router.IRouterAgent;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.router.IAgentRouterGraph;

@Component
public class RouterAgent extends AgentBase implements IRouterAgent {

    private final IAgentRouterGraph agentRouterGraph;
    private final IAgentStaticContext agentStaticContext;

    public RouterAgent(
            IAgentRouterGraph agentRouterGraph,
            IAgentStaticContext agentStaticContext) {

        this.agentRouterGraph = agentRouterGraph;
        this.agentStaticContext = agentStaticContext;
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
    public IAgentGraph getAgentGraph() {
        return this.agentRouterGraph;
    }

    @Override
    public IAgentStaticContext getAgentStaticContext() {
        return this.agentStaticContext;
    }

}
