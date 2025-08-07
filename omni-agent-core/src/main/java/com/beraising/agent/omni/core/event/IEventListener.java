package com.beraising.agent.omni.core.event;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public interface IEventListener {

        void beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void afterAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void beforeGraphInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void afterGraphInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void beforeGraphNodeInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void afterGraphNodeInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        IAgentEvent getAgentEvent();

        void setAgentEvent(IAgentEvent agentEvent);

}
