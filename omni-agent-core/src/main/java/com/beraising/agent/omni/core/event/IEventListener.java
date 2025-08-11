package com.beraising.agent.omni.core.event;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.session.IAgentSession;

public interface IEventListener {

        IAgentSession onStart(IAgent agent, IAgentEvent agentEvent)
                        throws Exception;

        void onError(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        Throwable throwable);

        IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph)
                        throws Exception;

        void beforeGraphInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void beforeGraphNodeInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void afterGraphNodeInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                        throws Exception;

        void onComplete(IAgent agent, IAgentSession agentSession, IAgentResponse agentResponse)
                        throws Exception;

}
