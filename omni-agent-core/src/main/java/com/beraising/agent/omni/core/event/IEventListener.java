package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;

public interface IAgentListener {

        void beforeInvoke(IAgent agent, IAgentEvent agentEvent)
                        throws Exception;

        void afterInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        IAgentEvent getAgentEvent();

        void setAgentEvent(IAgentEvent agentEvent);

}
