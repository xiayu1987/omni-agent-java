package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentListener {

    void beforeInvoke(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext) throws Exception;

    void afterInvoke(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext) throws Exception;

}
