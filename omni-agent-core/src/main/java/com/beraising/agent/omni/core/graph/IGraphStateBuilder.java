package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.beraising.agent.omni.core.agents.IAgentRequest;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IGraphStateBuilder<T extends IGraphState> {

    T build(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext);

    KeyStrategyFactory getKeyStrategyFactory();

}
