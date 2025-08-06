package com.beraising.agent.omni.core.graph;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public abstract class GraphBase<T extends IGraphState> implements IGraph {

    private IAgent agent;

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    @Override
    public void invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception {

        if (agentRuntimeContext.getAgentEvents().size() == 0) {
            throw new Exception("AgentRuntimeContext has no AgentEvent");
        }

        if (agentRuntimeContext.getAgentEvents().size() == 1) {
            agentRuntimeContext.getCompiledGraph()
                    .invoke(agentRuntimeContext.getGraphState().createInput(agentRuntimeContext));
        }

        if (agentRuntimeContext.getAgentEvents().size() > 1) {
            agentRuntimeContext.getCompiledGraph()
                    .invoke(agentRuntimeContext.getGraphState().createInput(agentRuntimeContext));
        }

    }

}
