package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

@Component
public class AgentSessionManage implements IAgentSessionManage {

    private List<IAgentSession> agentSessions;

    public AgentSessionManage() {
        super();
        this.agentSessions = new ArrayList<>();
    }

    @Override
    public List<IAgentSession> getAgentSessions() {
        return this.agentSessions;
    }

    @Override
    public IAgentSession getAgentSessionById(String sessionId) {
        return this.agentSessions.stream().filter(agentSession -> agentSession.getAgentSessionId().equals(sessionId))
                .findFirst().orElse(null);
    }

    @Override
    public IAgentSession createAgentSession() {
        IAgentSession newSession = new AgentSession();
        newSession.setAgentSessionId(UUID.randomUUID().toString());
        this.agentSessions.add(newSession);
        return newSession;
    }

    @Override
    public IAgentSession createAndAddAgentSession() {
        IAgentSession agentSession = createAgentSession();
        agentSessions.add(agentSession);
        return agentSession;
    }

    @Override
    public IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession) {
        return agentSession.getAgentSessionItems().stream()
                .filter(item -> item.isCurrent()).findFirst().orElse(null);
    }

    @Override
    public void clearAllSessions() {
        this.agentSessions.clear();
    }

    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        agentSession.getAgentSessionItems().add(sessionItem);
    }

}
