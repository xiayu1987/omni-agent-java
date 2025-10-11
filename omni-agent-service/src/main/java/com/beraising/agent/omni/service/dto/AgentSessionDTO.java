package com.beraising.agent.omni.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionDTO {

    private String agentSessionId;
    private String userId;
    private List<AgentSessionItemDTO> agentSessionItems;

}
