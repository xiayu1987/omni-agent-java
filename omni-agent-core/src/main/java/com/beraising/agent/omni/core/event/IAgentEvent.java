package com.beraising.agent.omni.core.event;

public interface IAgentEvent {

    IAgentRequest getAgentRequest();

    void setAgentRequest(IAgentRequest agentRequest);

    IAgentResponse getAgentResponse();

    void setAgentResponse(IAgentResponse agentResponse);

    String getAgentSessionId();

    void setAgentSessionId(String agentSessionId);

    String getUserId();

    void setUserId(String userId);

    boolean isStream();

    void setStream(boolean stream);

    void setSseChanel(ISseChanel sseChanel);

    ISseChanel getSseChanel();

    IAgentEvent copy();

}
