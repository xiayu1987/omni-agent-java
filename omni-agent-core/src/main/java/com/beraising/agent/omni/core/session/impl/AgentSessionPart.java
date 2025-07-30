package com.beraising.agent.omni.core.session.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSessionPart;

@Component
public class AgentSessionPart implements IAgentSessionPart {

    private IAgent agent;
    private IAgentRuntimeContext agentRuntimeContext;

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

}
