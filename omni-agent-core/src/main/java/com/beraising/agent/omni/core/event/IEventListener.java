package com.beraising.agent.omni.core.event;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IEventListener {

        void beforeInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void afterInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        IAgentEvent getAgentEvent();

        void setAgentEvent(IAgentEvent agentEvent);

}
