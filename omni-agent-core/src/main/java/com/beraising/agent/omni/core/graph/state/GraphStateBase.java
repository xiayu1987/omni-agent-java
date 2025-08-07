package com.beraising.agent.omni.core.graph.state;

import com.alibaba.cloud.ai.graph.OverAllState;

public abstract class GraphStateBase implements IGraphState {

    private OverAllState state;

    public OverAllState getState() {
        return state;
    }

    public void setState(OverAllState state) {
        this.state = state;
    }

}
