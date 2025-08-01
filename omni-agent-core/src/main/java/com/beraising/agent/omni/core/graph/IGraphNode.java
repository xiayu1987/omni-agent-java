package com.beraising.agent.omni.core.graph;

public interface IGraphNode<T extends IGraphState> {

    IGraph<T> getGraph();

    T getGraphState();
}
