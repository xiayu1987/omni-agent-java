package com.beraising.agent.omni.core.session;

import java.util.List;

public interface IAgentSession {

    String getAgentSessionId();

    void setAgentSessionId(String agentSessionId);

    List<IAgentSessionItem> getAgentSessionItems();

    void setAgentSessionItems(List<IAgentSessionItem> agentSessionItems);
}
