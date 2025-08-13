package com.beraising.agent.omni.core.graph.edge.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.edge.GraphEdgeBase;
import com.beraising.agent.omni.core.graph.edge.IConditionalEdge;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public class ConditionalEdge<T extends IGraphState> extends GraphEdgeBase<T> implements IConditionalEdge {

    private final IConditionalEdgeHandle<T> handle;

    public ConditionalEdge(String name, IAgentGraph graph, IConditionalEdgeHandle<T> handle) {
        super(name, graph);
        this.handle = handle;
    }

    @Override
    public String apply(T graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent)
            throws Exception {
        return handle.handle(graphState, agentRuntimeContext, agentEvent);
    }

    public interface IConditionalEdgeHandle<T extends IGraphState> {

        String handle(T graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent)
                throws Exception;

    }

}
