package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

public class AgentSession implements IAgentSession {

    private String agentSessionId;
    private List<IAgentSessionItem> agentSessionItems;
    private List<IAgentRuntimeContext> agentRuntimeContexts;

    public AgentSession() {
        super();
        this.agentSessionItems = new ArrayList<>();
        this.agentRuntimeContexts = new ArrayList<>();
    }

    @Override
    public String getAgentSessionId() {
        return agentSessionId;
    }

    @Override
    public void setAgentSessionId(String agentSessionId) {
        this.agentSessionId = agentSessionId;
    }

    @Override
    public List<IAgentSessionItem> getAgentSessionItems() {
        return agentSessionItems;
    }

    @Override
    public void setAgentSessionItems(List<IAgentSessionItem> agentSessionItems) {
        this.agentSessionItems = agentSessionItems;
    }

    @Override
    public List<IAgentRuntimeContext> getAgentRuntimeContexts() {
        return this.agentRuntimeContexts;
    }

    @Override
    public void setAgentRuntimeContexts(List<IAgentRuntimeContext> agentRuntimeContexts) {
        this.agentRuntimeContexts = agentRuntimeContexts;
    }

}
