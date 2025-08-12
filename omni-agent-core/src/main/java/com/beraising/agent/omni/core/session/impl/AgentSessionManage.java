package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
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
    public IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession) {

        return ListUtils.lastOf(agentSession.getAgentSessionItems());

    }

    @Override
    public void clearAllSessions() {
        this.agentSessions.clear();
    }

    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        agentSession.getAgentSessionItems().add(sessionItem);
    }

    @Override
    public void addAgentRuntimeContext(IAgentRuntimeContext runtimeContext) {
        IAgentSession currentSession = getAgentSessionById(runtimeContext.getAgentSessionID());
        if (currentSession != null) {
            currentSession.getAgentRuntimeContexts().add(runtimeContext);
        }
    }

    @Override
    public void addSession(IAgentSession agentSession) {
        agentSessions.add(agentSession);
    }

    @Override
    public IAgentRuntimeContext getAgentRuntimeContextById(String runtimeContextId) {
        for (IAgentSession session : agentSessions) {
            for (IAgentRuntimeContext context : session.getAgentRuntimeContexts()) {
                if (context.getAgentRuntimeContextID().equals(runtimeContextId)) {
                    return context;
                }
            }
        }
        return null;
    }

}
