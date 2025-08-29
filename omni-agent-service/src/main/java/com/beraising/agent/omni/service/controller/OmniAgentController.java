package com.beraising.agent.omni.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentRequest;
import com.beraising.agent.omni.service.dto.AgentEventDTO;
import com.beraising.agent.omni.service.dto.AgentResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/omni/agent")
public class OmniAgentController {

    private IAgentEngine omniAgentEngine;

    private static final Logger logger = LoggerFactory.getLogger(OmniAgentController.class);

    public OmniAgentController(IAgentEngine omniAgentEngine) {
        super();
        this.omniAgentEngine = omniAgentEngine;
    }

    @PostMapping(value = "/invoke", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<IAgentEvent>> postMethodName(@RequestBody AgentEventDTO agentEventDTO) {

        IAgentEvent agentResult = null;
        AgentEventDTO result = null;
        Sinks.Many<ServerSentEvent<IAgentEvent>> sink = Sinks.many().unicast().onBackpressureBuffer();

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
                    .agentSessionID(agentEventDTO.getAgentSessionID())
                    .isStream(true)
                    .sseChanel(sink)
                    .build());

        //     result = AgentEventDTO.builder()
        //             .agentSessionID(agentResult.getAgentSessionID())
        //             .agentRequest(null)
        //             .agentResponse(AgentResponseDTO.builder()
        //                     .responseType(agentResult.getAgentResponse().getResponseType().getCode())
        //                     .responseData(agentResult.getAgentResponse().getResponseData())
        //                     .build())
        //             .build();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return sink.asFlux()
                .doOnCancel(() -> logger.info("Client disconnected from stream"))
                .doOnError(e -> logger.error("Error occurred during streaming", e));
    }

}
