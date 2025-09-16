package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentSessionManage {

    IAgentSession getAgentSessionById(String sessionId);

    List<IAgentSession> getAgentSessionByUserId(String userId);

    IAgentRuntimeContext getAgentRuntimeContextById(String sessionId, String runtimeContextId);

    IAgentSession createAgentSession(String userId);

    IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession);

    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

}
