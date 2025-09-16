package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

public interface IAgentEngine {

    IAgentSessionManage getAgentSessionManage();

    IAgentEvent invoke(IAgentEvent agentEvent) throws Exception;

    IAgentEvent invoke(IAgent agent, IAgentEvent agentEvent) throws Exception;

}
