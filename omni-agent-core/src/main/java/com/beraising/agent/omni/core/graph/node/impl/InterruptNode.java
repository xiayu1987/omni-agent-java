package com.beraising.agent.omni.core.graph.node.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.node.IInterruptNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public class InterruptNode<T extends IGraphState> extends GraphNodeBase<T> implements IInterruptNode {

    public InterruptNode(String name, IAgentGraph graph) {
        super(name, graph);
    }

    @Override
    public IUpdatedGraphState<T> apply(T graphState,
            IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) throws Exception {
        getGraph().getAgentGraphListener().onInterrupt(getGraph().getAgent(), agentEvent, agentRuntimeContext, this,
                null);

        return null;
    }
}
