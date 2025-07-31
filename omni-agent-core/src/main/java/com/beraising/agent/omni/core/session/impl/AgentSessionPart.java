package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSessionPart;

public class AgentSessionPart implements IAgentSessionPart {

    private IAgent agent;
    private IAgentRuntimeContext agentRuntimeContext;
    private boolean current;

    public IAgent getAgent() {
        return agent;
    }

    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    public IAgentRuntimeContext getAgentRuntimeContext() {
        return agentRuntimeContext;
    }

    public void setAgentRuntimeContext(IAgentRuntimeContext agentRuntimeContext) {
        this.agentRuntimeContext = agentRuntimeContext;
    }

    @Override
    public boolean isCurrent() {
        return current;
    }

    @Override
    public void setCurrent(boolean current) {
        this.current = current;
    }

}
