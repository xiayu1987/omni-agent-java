package com.beraising.agent.omni.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.ISseChanel;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentRequest;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.service.dto.AgentEventDTO;
import com.beraising.agent.omni.service.dto.AgentRequestDTO;
import com.beraising.agent.omni.service.dto.AgentResponseDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public Flux<ServerSentEvent<AgentEventDTO>> postMethodName(@RequestBody AgentEventDTO agentEventDTO) {

        // IAgentEvent agentResult = null;
        // AgentEventDTO result = null;
        Sinks.Many<ServerSentEvent<AgentEventDTO>> sink = Sinks.many().unicast().onBackpressureBuffer();

        try {

            // agentResult =
            omniAgentEngine.invoke(AgentEvent.builder()
                    .agentRequest(
                            AgentRequest.builder()
                                    .requestType(
                                            EAgentRequestType.values()[agentEventDTO.getAgentRequest()
                                                    .getRequestType()])
                                    .requestData(agentEventDTO.getAgentRequest().getRequestData())
                                    .build())
                    .agentResponse(null)
                    .agentSessionId(agentEventDTO.getAgentSessionId())
                    .userId(agentEventDTO.getUserId())
                    .isStream(true)
                    .sseChanel(new ISseChanel() {

                        @Override
                        public void tryEmitNext(IAgentEvent agentEvent) {
                            sink.tryEmitNext(ServerSentEvent.<AgentEventDTO>builder().data(AgentEventDTO.builder()
                                    .agentSessionId(agentEvent.getAgentSessionId())
                                    .agentRequest(AgentRequestDTO.builder()
                                            .requestType(agentEvent.getAgentRequest().getRequestType().getCode())
                                            .requestData(agentEvent.getAgentRequest().getRequestData())
                                            .build())
                                    .agentResponse(AgentResponseDTO.builder()
                                            .responseType(agentEvent.getAgentResponse().getResponseType().getCode())
                                            .responseData(agentEvent.getAgentResponse().getResponseData())
                                            .build())
                                    .build()).build());
                        }

                        @Override
                        public void tryEmitError(Throwable error) {
                            sink.tryEmitError(error);
                        }

                        @Override
                        public void tryEmitComplete() {
                            sink.tryEmitComplete();
                        }

                    })
                    .build());

            // result = AgentEventDTO.builder()
            // .agentSessionID(agentResult.getAgentSessionID())
            // .agentRequest(null)
            // .agentResponse(AgentResponseDTO.builder()
            // .responseType(agentResult.getAgentResponse().getResponseType().getCode())
            // .responseData(agentResult.getAgentResponse().getResponseData())
            // .build())
            // .build();
        } catch (Exception e) {

            e.printStackTrace();
        }
        // ServerSentEvent.<IAgentEvent>builder().data(agentEvent).build()
        return sink.asFlux()
                .doOnCancel(() -> logger.info("Client disconnected from stream"))
                .doOnError(e -> logger.error("Error occurred during streaming", e));
    }

    // ========= 新增方法：根据 userId 获取用户的全部 session =========
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<IAgentSession>> getSessionsByUserId(@PathVariable("userId") String userId) {
        try {
            List<IAgentSession> sessions = omniAgentEngine.getAgentSessionManage().getAgentSessionByUserId(userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            logger.error("Failed to get sessions by userId {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========= 删除指定 session =========
    @DeleteMapping("/sessions/{userId}/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable("userId") String userId,
            @PathVariable("sessionId") String sessionId) {
        try {
            // omniAgentEngine.getAgentSessionManage().deleteSession(userId, sessionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to delete session {} for user {}", sessionId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========= 重命名会话 =========
    @PutMapping("/sessions/{userId}/{sessionId}")
    public ResponseEntity<Void> renameSession(
            @PathVariable("userId") String userId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody Map<String, String> body) {
        try {
            String newTitle = body.get("title");
            // omniAgentEngine.getAgentSessionManage().renameSession(userId, sessionId,
            // newTitle);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to rename session {} for user {}", sessionId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
