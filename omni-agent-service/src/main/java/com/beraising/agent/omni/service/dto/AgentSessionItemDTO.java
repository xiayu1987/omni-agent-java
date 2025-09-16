package com.beraising.agent.omni.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItemDTO {

    private AgentRequestDTO agentRequest;
    private AgentResponseDTO agentResponse;
    private String agentName;

}
