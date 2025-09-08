package com.beraising.agent.omni.core.event.impl;

import org.springframework.http.codec.ServerSentEvent;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Sinks.Many;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvent implements IAgentEvent {

    private IAgentRequest agentRequest;
    private IAgentResponse agentResponse;
    private String agentSessionID;
    private boolean isStream;
    @JsonIgnore
    private transient Many<ServerSentEvent<IAgentEvent>> sseChanel;

    @Override
    public IAgentEvent copy() {
        AgentEvent copy = new AgentEvent();
        copy.setAgentRequest(agentRequest != null ? agentRequest.copy() : null);
        copy.setAgentResponse(agentResponse != null ? agentResponse.copy() : null);
        copy.setAgentSessionID(agentSessionID);
        copy.setStream(isStream);
        copy.setSseChanel(sseChanel);
        return copy;
    }

}
