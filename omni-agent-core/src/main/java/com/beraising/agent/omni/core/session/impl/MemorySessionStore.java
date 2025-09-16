package com.beraising.agent.omni.core.session.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.beraising.agent.omni.core.session.ISessionStore;

@Component("memory")
public class MemorySessionStore implements ISessionStore {
    private final Map<String, IAgentSession> store = new ConcurrentHashMap<>();

    @Override
    public IAgentSession selectById(String sessionId) {
        return store.get(sessionId);
    }

    @Override
    public List<IAgentSession> selectByUserId(String userId) {
        return store.values().stream().filter(session -> session.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void saveSession(IAgentSession session) {
        store.put(session.getAgentSessionId(), session);
    }

    @Override
    public void delete(String sessionId) {
        store.remove(sessionId);
    }

    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession == null || sessionItem == null) {
            return;
        }
        agentSession.getAgentSessionItems().add(sessionItem);
    }

    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null) {
            return;
        }
        agentSession.getAgentRuntimeContexts().add(runtimeContext);
    }

}
