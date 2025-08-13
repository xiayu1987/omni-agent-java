package com.beraising.agent.omni.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentRequest;
import com.beraising.agent.omni.service.dto.AgentEventDTO;
import com.beraising.agent.omni.service.dto.AgentResponseDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/omni/agent")
public class OmniAgentController {

    private IAgentEngine omniAgentEngine;

    public OmniAgentController(IAgentEngine omniAgentEngine) {
        super();
        this.omniAgentEngine = omniAgentEngine;
    }

    @PostMapping("/invoke")
    public AgentEventDTO postMethodName(@RequestBody AgentEventDTO agentEventDTO) {

        IAgentEvent agentResult = null;
        AgentEventDTO result = null;

        try {
            agentResult = omniAgentEngine.invoke(AgentEvent.builder()
                    .agentRequest(
                            AgentRequest.builder()
                                    .requestType(
                                            EAgentRequestType.values()[agentEventDTO.getAgentRequest()
                                                    .getRequestType()])
                                    .requestData(agentEventDTO.getAgentRequest().getRequestData())
                                    .build())
                    .agentResponse(null)
                    .agentSessionID(agentEventDTO.getAgentSessionID()).build());

            result = AgentEventDTO.builder().agentRequest(null)
                    .agentResponse(AgentResponseDTO.builder()
                            .responseType(agentResult.getAgentResponse().getResponseType().getCode())
                            .responseData(agentResult.getAgentResponse().getResponseData())
                            .build())
                    .build();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

}
