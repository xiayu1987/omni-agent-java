package com.beraising.agent.omni.core.session;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface ISessionStore {

    IAgentSession selectById(String sessionId);

    void saveSession(IAgentSession session);

    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

    void delete(String sessionId);
}
