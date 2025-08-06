package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

public class EventListener implements IEventListener {
    private IAgentSessionManage agentSessionManage;
    private IAgentEvent agentEvent;

    public EventListener(IAgentEvent agentEvent, IAgentSessionManage agentSessionManage) {
        super();
        this.agentEvent = agentEvent;
        this.agentSessionManage = agentSessionManage;
    }

    @Override
    public IAgentEvent getAgentEvent() {

        return this.agentEvent;
    }

    @Override
    public void setAgentEvent(IAgentEvent agentEvent) {
        this.agentEvent = agentEvent;
    }

    @Override
    public void beforeInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
            throws Exception {
        agentSessionManage.addAgentRuntimeContext(agentRuntimeContext);
    }

    @Override
    public void afterInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
            throws Exception {
        agentEvent.setAgentResponse(null);
    }

}
