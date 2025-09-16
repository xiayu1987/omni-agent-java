package com.beraising.agent.omni.core.session.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.ISessionStore;
import com.beraising.agent.omni.core.session.config.AgentSessionProperties;
import com.beraising.agent.omni.core.session.config.AgentSessionProperties.StorageType;

import jakarta.annotation.PostConstruct;

import com.beraising.agent.omni.core.session.IAgentSessionItem;

@Component
public class AgentSessionManage implements IAgentSessionManage {

    private final Map<String, ISessionStore> sessionStoreMap;
    private final AgentSessionProperties properties;
    private ISessionStore storage;

    public AgentSessionManage(Map<String, ISessionStore> sessionStoreMap,
            AgentSessionProperties properties) {
        this.sessionStoreMap = sessionStoreMap;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        StorageType storageType = properties.getStoreType();
        this.storage = sessionStoreMap.get(storageType.name().toLowerCase());

        if (this.storage == null) {
            throw new IllegalStateException(
                    "No ISessionStore found for type: '" + storageType + "'. " +
                            "Available: " + sessionStoreMap.keySet());
        }
    }

    @Override
    public IAgentSession getAgentSessionById(String sessionId) {
        return this.storage.selectById(sessionId);
    }

    @Override
    public List<IAgentSession> getAgentSessionByUserId(String userId) {
        return this.storage.selectByUserId(userId);
    }

    @Override
    public IAgentSession createAgentSession(String userId) {
        IAgentSession newSession = new AgentSession();
        newSession.setAgentSessionId(UUID.randomUUID().toString());
        newSession.setUserId(userId);

        this.storage.saveSession(newSession);

        return newSession;
    }

    @Override
    public IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession) {
        return ListUtils.lastOf(agentSession.getAgentSessionItems());
    }

    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession != null && sessionItem != null) {
            this.storage.addSessionItem(agentSession, sessionItem);
        }
    }

    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession != null && runtimeContext != null) {
            this.storage.addAgentRuntimeContext(agentSession, runtimeContext);
        }
    }

    @Override
    public IAgentRuntimeContext getAgentRuntimeContextById(String sessionId, String runtimeContextId) {
        IAgentSession agentSession = getAgentSessionById(sessionId);
        return agentSession.getAgentRuntimeContexts().stream()
                .filter(agentRuntimeContext -> agentRuntimeContext.getAgentRuntimeContextId().equals(runtimeContextId))
                .findFirst().orElse(null);
    }

}
