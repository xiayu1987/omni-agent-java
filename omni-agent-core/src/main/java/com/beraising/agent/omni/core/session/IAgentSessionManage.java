package com.beraising.agent.omni.core.session;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentSessionManage {

    IAgentSession getAgentSessionById(String sessionId);

    IAgentRuntimeContext getAgentRuntimeContextById(String sessionId, String runtimeContextId);

    IAgentSession createAgentSession();

    IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession);

    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

}
