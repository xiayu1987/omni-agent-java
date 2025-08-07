package com.beraising.agent.omni.core.graph.state;

import java.util.Map;

public interface IUpdatedGraphState<T> {

    Map<String, Object> exec();

}
