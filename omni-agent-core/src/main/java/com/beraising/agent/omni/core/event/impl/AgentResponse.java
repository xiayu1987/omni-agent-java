package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse implements IAgentResponse {

    private EAgentResponseType responseType;
    private String responseData;

    @Override
    public IAgentResponse copy() {

        return AgentResponse.builder()
                .responseType(responseType)
                .responseData(responseData)
                .build();
    }

}
