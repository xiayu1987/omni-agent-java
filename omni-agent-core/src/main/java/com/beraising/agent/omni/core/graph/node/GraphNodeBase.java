package com.beraising.agent.omni.core.graph.node;

import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.GraphPartBase;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public abstract class GraphNodeBase<T extends IGraphState> extends GraphPartBase<T> implements NodeAction, IGraphNode {

    public GraphNodeBase(String name, IAgentGraph graph) {
        super(name, graph);

        getGraph().getGraphNodes().add(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {

        StateInfo stateInfo = getGraphState(state);

        IUpdatedGraphState<T> updatedGraphState = apply((T) stateInfo.getGraphState(),
                stateInfo.getAgentRuntimeContext(),
                stateInfo.getAgentEvent());

        getGraph().onGraphPartApplield(this, updatedGraphState, stateInfo.getGraphState(), stateInfo.getAgentRuntimeContext(), stateInfo.getAgentEvent());

        return updatedGraphState.exec();
    }

    public abstract IUpdatedGraphState<T> apply(T graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception;

}
