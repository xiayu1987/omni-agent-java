package com.beraising.agent.omni.core.graph.edge;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.GraphPartBase;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public abstract class GraphEdgeBase<T extends IGraphState> extends GraphPartBase<T> implements EdgeAction, IGraphEdge {

    public GraphEdgeBase(String name, IAgentGraph graph) {
        super(name, graph);

        getGraph().getGraphEdges().add(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String apply(OverAllState state) throws Exception {

        StateInfo stateInfo = getGraphState(state);

        return apply((T) stateInfo.getGraphState(), stateInfo.getAgentRuntimeContext(), stateInfo.getAgentEvent());
    }

    public abstract String apply(T graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception;
}
