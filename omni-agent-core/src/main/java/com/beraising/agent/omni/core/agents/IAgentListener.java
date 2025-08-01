package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentListener {

    void beforeInvoke() throws Exception;

    void afterInvoke(IAgentRuntimeContext agentRuntimeContext) throws Exception;

}
