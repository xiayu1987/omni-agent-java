package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.event.IAgentEvent;

public interface IAgentEngine {

    void invoke(IAgentEvent agentEvent) throws Exception;

    void invoke(IAgent agent, IAgentEvent agentEvent) throws Exception;

}
