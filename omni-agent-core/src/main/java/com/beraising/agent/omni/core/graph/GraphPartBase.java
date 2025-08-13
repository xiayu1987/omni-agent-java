package com.beraising.agent.omni.core.graph;

import org.springframework.ai.chat.client.ChatClient;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.session.IAgentSession;

public abstract class GraphPartBase<T extends IGraphState> implements IGraphPart {

    private IAgentGraph graph;
    private String name;

    public GraphPartBase(String name, IAgentGraph graph) {
        this.name = name;
        this.graph = graph;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IAgentGraph getGraph() {
        return graph;
    }

    protected ChatClient.Builder getChatClientBuilder() {
        return graph.getAgent().getAgentStaticContext().getChatClientBuilder();
    }

    protected ChatClient getChatClient() {
        return graph.getAgent().getAgentStaticContext().getChatClientBuilder().build();
    }

    protected IAgentStaticContext getAgentStaticContext() {
        return graph.getAgent().getAgentStaticContext();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StateInfo getGraphState(OverAllState state) throws Exception {
        String sessionId = state.value(IGraphState.getAgentSessionIDKey(), "");

        if (sessionId == null) {
            throw new Exception("Session ID cannot be null");
        }

        IAgentSession agentSession = graph.getAgent().getAgentStaticContext().getAgentSessionManage()
                .getAgentSessionById(sessionId);

        if (agentSession == null) {
            throw new Exception("Agent session not found for session ID: " + sessionId);

        }

        IAgentRuntimeContext agentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());

        if (agentRuntimeContext == null) {
            throw new Exception("Agent runtime context not found for session ID: " + sessionId);
        }

        IGraphState graphState = agentRuntimeContext.getGraphState();

        if (graphState == null) {
            throw new Exception("Graph state not found for session ID: " + sessionId);
        }

        graphState.setState(state);

        IAgentEvent agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());

        return new StateInfo((T) graphState, agentRuntimeContext, agentEvent);
    }
}
