package com.beraising.agent.omni.core.graph;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.beraising.agent.omni.core.context.IAgentStaticContext;

public abstract class GraphNodeBase<T extends IGraphState> implements NodeAction, IGraphNode<T> {

    private IGraph<T> graph;

    public GraphNodeBase(IGraph<T> graph) {
        this.graph = graph;
    }

    public IGraph<T> getGraph() {
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
        return apply(getGraphState(state)).exec();
    }

    public abstract IUpdatedGraphState<T> apply(T state) throws Exception;

    public T getGraphState(OverAllState state) {
        graph.getGraphState().setState(state);
        return graph.getGraphState();
    }

    @Override
    public T getGraphState() {
        return graph.getGraphState();
    }

}
