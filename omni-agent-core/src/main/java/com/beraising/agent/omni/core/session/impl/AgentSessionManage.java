package com.beraising.agent.omni.core.session.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.IAgentSessionPart;
import com.beraising.agent.omni.core.session.IAgentSessionUser;

@Component
public class AgentSessionManage implements IAgentSessionManage {

    private List<IAgentSession> agentSessions;

    @Override
    public List<IAgentSession> getAgentSessions() {
        return this.agentSessions;
    }

    @Override
    public List<IAgentSession> getAgentSessionsByUser(IAgentSessionUser user) {
        return this.agentSessions.stream().filter(agentSession -> agentSession.getAgentSessionUser().equals(user))
                .toList();
    }

    @Override
    public IAgentSession getAgentSessionById(String sessionId) {
        return this.agentSessions.stream().filter(agentSession -> agentSession.getAgentSessionId().equals(sessionId))
                .findFirst().orElse(null);
    }

    @Override
    public IAgentSession createAgentSession(IAgentSessionUser user) {
        IAgentSession newSession = new AgentSession();
        newSession.setAgentSessionId(UUID.randomUUID().toString());
        newSession.setAgentSessionUser(user);
        this.agentSessions.add(newSession);
        return newSession;
    }

    @Override
    public void clearAllSessions() {
        this.agentSessions.clear();
    }

    @Override
    public IAgentSession createAndAddAgentSession(IAgentSessionUser user) {
        IAgentSession agentSession = createAgentSession(user);
        agentSessions.add(agentSession);
        return agentSession;
    }

    @Override
    public IAgentSessionPart getCurrentSessionPart(IAgentSession agentSession) {
        return agentSession.getAgentSessionParts().stream()
                .filter(part -> part.isCurrent()).findFirst().orElse(null);
    }

}
