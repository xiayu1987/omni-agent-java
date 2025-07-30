package com.beraising.agent.omni.core.graph;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.beraising.agent.omni.core.agents.AgentContext;

public abstract class GraphNodeBase<T extends IGraphState> implements NodeAction {

    private IGraph graph;

    public GraphNodeBase(IGraph graph) {
        this.graph = graph;
    }

    public IGraph getGraph() {
        return graph;
    }

    protected ChatClient.Builder getChatClientBuilder() {
        return graph.getAgent().getAgentContext().getChatClientBuilder();
    }

    protected ChatClient getChatClient() {
        return graph.getAgent().getAgentContext().getChatClientBuilder().build();
    }

    protected AgentContext getAgentContext() {
        return graph.getAgent().getAgentContext();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        return apply(getGraphState(state)).exec();
    }

    public abstract IUpdatedGraphState<T> apply(T state) throws Exception;

    public abstract T getGraphState(OverAllState state) throws Exception;

}
