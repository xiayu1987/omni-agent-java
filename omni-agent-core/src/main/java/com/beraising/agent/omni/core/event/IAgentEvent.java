package com.beraising.agent.omni.core.event;

public interface IAgentEvent {

    IAgentRequest getAgentRequest();

    void setAgentRequest(IAgentRequest agentRequest);

    IAgentResponse getAgentResponse();

    void setAgentResponse(IAgentResponse agentResponse);

    String getAgentSessionID();

    void setAgentSessionID(String agentSessionID);

    String getParentAgentSessionID();

    void setParentAgentSessionID(String parentAgentSessionID);

    IAgentEvent copy();

}
