package com.beraising.agent.omni.core.graph;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.session.IAgentSession;

public abstract class GraphNodeBase<T extends IGraphState> implements NodeAction, IGraphNode {

    private IGraph graph;

    public GraphNodeBase(IGraph graph) {
        this.graph = graph;
    }

    public IGraph getGraph() {
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

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        StateInfo stateInfo = getGraphState(state);

        return apply(stateInfo.getGraphState(), stateInfo.getAgentRuntimeContext(), stateInfo.getAgentEvent()).exec();
    }

    public abstract IUpdatedGraphState<T> apply(T graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception;

    @SuppressWarnings("unchecked")
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

    private class StateInfo {
        private T graphState;
        private IAgentRuntimeContext agentRuntimeContext;
        private IAgentEvent agentEvent;

        public StateInfo(T graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) {
            this.graphState = graphState;
            this.agentRuntimeContext = agentRuntimeContext;
            this.agentEvent = agentEvent;
        }

        public T getGraphState() {
            return graphState;
        }

        public IAgentRuntimeContext getAgentRuntimeContext() {
            return agentRuntimeContext;
        }

        public IAgentEvent getAgentEvent() {
            return agentEvent;
        }
    }

}
