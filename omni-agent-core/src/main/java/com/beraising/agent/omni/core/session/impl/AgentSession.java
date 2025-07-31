package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionPart;
import com.beraising.agent.omni.core.session.IAgentSessionUser;

public class AgentSession implements IAgentSession {

    private String agentSessionId;
    private List<IAgentSessionPart> agentSessionParts;
    private IAgentSessionUser agentSessionUser;

    public AgentSession() {
        super();
        agentSessionParts = new ArrayList<>();
    }

    @Override
    public String getAgentSessionId() {
        return agentSessionId;
    }

    @Override
    public void setAgentSessionId(String agentSessionId) {
        this.agentSessionId = agentSessionId;
    }

    public List<IAgentSessionPart> getAgentSessionParts() {
        return agentSessionParts;
    }

    public void setAgentSessionParts(List<IAgentSessionPart> agentSessionParts) {
        this.agentSessionParts = agentSessionParts;
    }

    @Override
    public IAgentSessionUser getAgentSessionUser() {
        return agentSessionUser;
    }

    @Override
    public void setAgentSessionUser(IAgentSessionUser agentSessionUser) {
        this.agentSessionUser = agentSessionUser;
    }

}
