package com.beraising.agent.omni.core.agents;

import org.springframework.stereotype.Component;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.router.IAgentRouterGraph;

@Component
public class OmniAgent extends AgentBase {

    private final IAgentRouterGraph agentRouterGraph;
    private final AgentContext agentContext;

    public OmniAgent(AgentContext agentContext, IAgentRouterGraph agentRouterGraph) {
        this.agentRouterGraph = agentRouterGraph;
        this.agentContext = agentContext;
    }

    @Override
    public String getName() {
        return "Omni Agent";
    }

    @Override
    public String getDescription() {
        return "Agent总入口用于调度所有Agent";
    }

    @Override
    public IGraph getGraph() {
        return this.agentRouterGraph;
    }

    @Override
    public AgentContext getAgentContext() {
        return this.agentContext;
    }

}
