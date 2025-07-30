package com.beraising.agent.omni.core.graph.form.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.graph.GraphNodeBase;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.form.state.FormState;

public class FormChooseNode  extends GraphNodeBase<FormState>{

    public FormChooseNode(IGraph graph) {
        super(graph);
    }

    @Override
    public IUpdatedGraphState<FormState> apply(FormState state) throws Exception {
        return null;
    }

    @Override
    public FormState getGraphState(OverAllState state) throws Exception {
        return new FormState(state);
    }

}
