package com.beraising.agent.omni.core.graph;

import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.OverAllState;

public interface IGraphState {

    OverAllState getState();

    void setState(OverAllState state);

    Map<String, Object> createUserQuery(String userQuery);

    KeyStrategyFactory getKeyStrategyFactory();
}
