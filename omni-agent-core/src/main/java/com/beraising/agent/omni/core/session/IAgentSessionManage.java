package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentSessionManage {

    List<IAgentSession> getAgentSessions();

    IAgentSession getAgentSessionById(String sessionId);

    IAgentSession createAgentSession();

    IAgentSession createAndAddAgentSession();

    IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession);

    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    void addAgentRuntimeContext(IAgentRuntimeContext runtimeContext);

    void clearAllSessions();

}
