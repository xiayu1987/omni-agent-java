package com.beraising.agent.omni.core.event;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.session.IAgentSession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IEventListener {

        IAgentSession onStart(IAgent agent, IAgentEvent agentEvent);

        IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph);

        void onInvokeStream(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        AgentGraphInvokeStreamContent content);

        void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        IAgentResponse agentResponse);

        void onError(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        Throwable throwable);

        void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        IAgentResponse agentResponse);

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public class AgentGraphInvokeStreamContent {

                private boolean isError;
                private boolean isComplete;
                private String content;

        }

}
