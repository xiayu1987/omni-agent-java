package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;

public interface IAgentSession {

    String getAgentSessionId();

    void setAgentSessionId(String agentSessionId);

    String getParentSessionId();

    void setParentSessionId(String parentSessionId);

    EUserType getUserType();

    void setUserType(EUserType userType);

    String getUserId();

    void setUserId(String userId);

    List<IAgentSessionItem> getAgentSessionItems();

    void setAgentSessionItems(List<IAgentSessionItem> agentSessionItems);

    List<IAgentRuntimeContext> getAgentRuntimeContexts();

    IAgentRuntimeContext getCurrentRuntimeContext();

    void setAgentRuntimeContexts(List<IAgentRuntimeContext> agentRuntimeContexts);
}
