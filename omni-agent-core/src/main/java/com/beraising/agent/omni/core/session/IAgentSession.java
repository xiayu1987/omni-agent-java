package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentSession {

    String getAgentSessionId();

    void setAgentSessionId(String agentSessionId);

    String getUserId();

    void setUserId(String userId);

    List<IAgentSessionItem> getAgentSessionItems();

    void setAgentSessionItems(List<IAgentSessionItem> agentSessionItems);

    List<IAgentRuntimeContext> getAgentRuntimeContexts();

    void setAgentRuntimeContexts(List<IAgentRuntimeContext> agentRuntimeContexts);
}
