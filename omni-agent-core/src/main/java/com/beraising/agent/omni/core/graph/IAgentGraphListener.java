package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;

public interface IAgentGraphListener {

    void setStateGraphLifecycleListener(GraphLifecycleListener stateGraphLifecycleListener);

    GraphLifecycleListener getStateGraphLifecycleListener();

}
