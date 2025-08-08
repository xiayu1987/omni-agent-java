package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.event.IAgentEvent;

public interface IAgentEngine {

    IAgentEvent invoke(IAgentEvent agentEvent) throws Exception;

    IAgentEvent invoke(IAgent agent, IAgentEvent agentEvent) throws Exception;

}
