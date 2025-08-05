package com.beraising.agent.omni.core.context;

import com.beraising.agent.omni.core.event.IAgentEvent;

public interface IAgentRuntimeContextBuilder {

    IAgentRuntimeContext build(IAgentEvent agentEvent);

}
