package com.beraising.agent.omni.core.graph.form.nodes;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.form.state.FormState;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public class FormParamInitNode extends GraphNodeBase<FormState> {

    public FormParamInitNode(IAgentGraph graph) {
        super(graph);
    }

    @Override
    public IUpdatedGraphState<FormState> apply(FormState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

}
