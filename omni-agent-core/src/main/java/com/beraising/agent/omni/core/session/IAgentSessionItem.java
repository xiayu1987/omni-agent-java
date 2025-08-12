package com.beraising.agent.omni.core.session;

import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;

public interface IAgentSessionItem {

    String getAgentName();

    void setAgentName(String agentName);

    IAgentRequest getAgentRequest();

    void setAgentRequest(IAgentRequest agentRequest);

    IAgentResponse getAgentResponse();

    void setAgentResponse(IAgentResponse agentResponse);

}
