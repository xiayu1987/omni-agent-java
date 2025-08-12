package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.event.IAgentRequest;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItem implements IAgentSessionItem {

    private IAgentRequest agentRequest;
    private IAgentResponse agentResponse;
    private String agentName;

}
