package com.beraising.agent.omni.core.agents.impl;

import java.util.Map;

import com.beraising.agent.omni.core.agents.EAgentRequestType;
import com.beraising.agent.omni.core.agents.IAgentRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentRequest implements IAgentRequest {
    private EAgentRequestType requestType;
    private String text;
    private Map<String, Object> data;

}
