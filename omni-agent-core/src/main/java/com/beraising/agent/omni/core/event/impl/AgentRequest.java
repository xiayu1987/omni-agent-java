package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest implements IAgentRequest {
    private EAgentRequestType requestType;
    private String requestData;

    @Override
    public IAgentRequest copy() {

        return AgentRequest.builder()
                .requestType(requestType)
                .requestData(requestData)
                .build();
    }

}
