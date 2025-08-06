package com.beraising.agent.omni.core.event.impl;

import java.util.Map;

import com.beraising.agent.omni.core.agents.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentRequest implements IAgentRequest {
    private EAgentRequestType requestType;
    private String text;
    private Map<String, Object> data;

    @Override
    public IAgentRequest copy() {

        return AgentRequest.builder()
                .requestType(requestType)
                .text(text)
                .data(data != null ? new ObjectMapper().convertValue(data, new TypeReference<Map<String, Object>>() {
                }) : null)
                .build();
    }

}
