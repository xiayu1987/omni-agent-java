package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

public class AgentSession implements IAgentSession {

    private String agentSessionId;
    private List<IAgentSessionItem> agentSessionItems;

    public AgentSession() {
        super();
        agentSessionItems = new ArrayList<>();
    }

    @Override
    public String getAgentSessionId() {
        return agentSessionId;
    }

    @Override
    public void setAgentSessionId(String agentSessionId) {
        this.agentSessionId = agentSessionId;
    }

    public List<IAgentSessionItem> getAgentSessionItems() {
        return agentSessionItems;
    }

    public void setAgentSessionItems(List<IAgentSessionItem> agentSessionItems) {
        this.agentSessionItems = agentSessionItems;
    }


}
