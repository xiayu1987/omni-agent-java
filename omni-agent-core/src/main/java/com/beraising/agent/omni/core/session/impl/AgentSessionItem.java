package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

public class AgentSessionItem implements IAgentSessionItem {

    private IAgent agent;
    private boolean current;
    private IAgentRuntimeContext agentRuntimeContext;

    public IAgent getAgent() {
        return agent;
    }

    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public boolean isCurrent() {
        return current;
    }

    @Override
    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public IAgentRuntimeContext getAgentRuntimeContext() {
        return agentRuntimeContext;
    }

    @Override
    public void setAgentRuntimeContext(IAgentRuntimeContext agentRuntimeContext) {
        this.agentRuntimeContext = agentRuntimeContext;
    }

}
