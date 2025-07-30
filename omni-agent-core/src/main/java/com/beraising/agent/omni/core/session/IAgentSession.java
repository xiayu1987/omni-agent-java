package com.beraising.agent.omni.core.session;

import java.util.List;

public interface IAgentSession {

    List<IAgentSessionPart> getAgentSessionPart();

    void setAgentSessionPart(List<IAgentSessionPart> agentSessionPart);

}
