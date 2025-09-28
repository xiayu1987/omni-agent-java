package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentRequest;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItem implements IAgentSessionItem {

    @JsonDeserialize(as = AgentRequest.class)
    private IAgentRequest agentRequest;

    @JsonDeserialize(as = AgentResponse.class)
    private IAgentResponse agentResponse;
    private String agentName;

}
