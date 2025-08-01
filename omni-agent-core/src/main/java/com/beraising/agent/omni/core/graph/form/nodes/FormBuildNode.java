package com.beraising.agent.omni.core.graph.form.nodes;

import com.beraising.agent.omni.core.graph.GraphNodeBase;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.form.state.FormState;

public class FormBuildNode extends GraphNodeBase<FormState> {

    public FormBuildNode(IGraph<FormState> graph) {
        super(graph);
    }

    @Override
    public IUpdatedGraphState<FormState> apply(FormState state) throws Exception {
        return null;
    }

}
