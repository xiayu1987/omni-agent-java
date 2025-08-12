package com.beraising.agent.omni.core.agents;

import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IAgentGraph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IAgent {

    String getName();

    String getDescription();

    IAgentStaticContext getAgentStaticContext();

    IAgentGraph getAgentGraph();

    void init(IEventListener eventListener) throws Exception;

    IAgentEvent invoke(IAgentEvent agentEvent) throws Exception;

    FunctionToolCallback<AsToolRequest, AsToolResponse> asToolCallback(IAgentEvent agentEvent);

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AsToolRequest {
        private String sessionID;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AsToolResponse {
        private boolean isSuccess;
        private String errorMessage;
        private IAgentResponse agentResponse;
        private String resultFormat;
    }
}
