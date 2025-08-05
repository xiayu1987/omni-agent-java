package com.beraising.agent.omni.core.event;

import com.beraising.agent.omni.core.session.IAgentSession;

public interface IAgentEvent {

    IAgentRequest getAgentRequest();

    void setAgentRequest(IAgentRequest agentRequest);

    IAgentResponse getAgentResponse();

    void setAgentResponse(IAgentResponse agentResponse);

    public IAgentSession getAgentSession();

    public void setAgentSession(IAgentSession agentSession);

}
