package com.beraising.agent.omni.core.session;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IAgentSessionPart {

    boolean isCurrent();

    void setCurrent(boolean current);

    IAgent getAgent();

    void setAgent(IAgent agent);

    IAgentRuntimeContext getAgentRuntimeContext();

    void setAgentRuntimeContext(IAgentRuntimeContext agentRuntimeContext);

}
