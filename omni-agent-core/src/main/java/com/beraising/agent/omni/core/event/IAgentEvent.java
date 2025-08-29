package com.beraising.agent.omni.core.event;

import org.springframework.http.codec.ServerSentEvent;

import reactor.core.publisher.Sinks;

public interface IAgentEvent {

    IAgentRequest getAgentRequest();

    void setAgentRequest(IAgentRequest agentRequest);

    IAgentResponse getAgentResponse();

    void setAgentResponse(IAgentResponse agentResponse);

    String getAgentSessionID();

    void setAgentSessionID(String agentSessionID);

    boolean isStream();

    void setStream(boolean stream);

    void setSseChanel(Sinks.Many<ServerSentEvent<IAgentEvent>> sseChanel);

    Sinks.Many<ServerSentEvent<IAgentEvent>> getSseChanel();

    IAgentEvent copy();

}
