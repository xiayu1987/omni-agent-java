package com.beraising.agent.omni.core.session;

import com.beraising.agent.omni.core.agents.IAgent;

public interface IAgentSessionItem {

    IAgent getAgent();

    void setAgent(IAgent agent);

    boolean isSubSession();

    void setIsSubSession(boolean subSession);

    String getSubAgentSessionId();

    void setSubAgentSessionId(String subAgentSessionId);

}
