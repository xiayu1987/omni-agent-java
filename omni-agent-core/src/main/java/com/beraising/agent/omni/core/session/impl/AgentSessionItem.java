package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

public class AgentSessionItem implements IAgentSessionItem {

    private IAgent agent;
    private boolean subSession;
    private String subAgentSessionId;

    public IAgent getAgent() {
        return agent;
    }

    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public boolean isSubSession() {
        return subSession;
    }

    @Override
    public void setIsSubSession(boolean subSession) {
        this.subSession = subSession;

    }

    @Override
    public String getSubAgentSessionId() {
        return subAgentSessionId;
    }

    @Override
    public void setSubAgentSessionId(String subAgentSessionId) {
        this.subAgentSessionId = subAgentSessionId;
    }

}
