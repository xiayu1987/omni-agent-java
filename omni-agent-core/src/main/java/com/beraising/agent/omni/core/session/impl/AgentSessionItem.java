package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

public class AgentSessionItem implements IAgentSessionItem {

    private IAgent agent;
    private boolean subSession;
    private String subSessionId;

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
    public String getSubSessionId() {
        return subSessionId;
    }

    @Override
    public void setSubSessionId(String subSessionId) {
        this.subSessionId = subSessionId;
    }

}
