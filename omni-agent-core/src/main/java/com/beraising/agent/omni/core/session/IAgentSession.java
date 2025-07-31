package com.beraising.agent.omni.core.session;

import java.util.List;

public interface IAgentSession {

    String getAgentSessionId();

    void setAgentSessionId(String agentSessionId);

    List<IAgentSessionPart> getAgentSessionParts();

    void setAgentSessionParts(List<IAgentSessionPart> agentSessionParts);

    IAgentSessionUser getAgentSessionUser();

    void setAgentSessionUser(IAgentSessionUser agentSessionUser);
}
