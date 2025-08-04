package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.session.IAgentSession;

public interface IAgentEngine {

    void invoke(IAgentRequest agentRequest) throws Exception;

    void invoke(IAgentRequest agentRequest, IAgentSession agentSession) throws Exception;

    void invoke(IAgent agent, IAgentRequest agentRequest) throws Exception;

    void invoke(IAgent agent, IAgentRequest agentRequest, IAgentSession agentSession) throws Exception;

}
