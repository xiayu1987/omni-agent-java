package com.beraising.agent.omni.core.agents.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.impl.EventListener;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

@Component
public class OmniAgentEngine implements IAgentEngine {

    private final IAgentSessionManage agentSessionManage;
    private final AgentRegistry agentRegistry;

    public OmniAgentEngine(AgentRegistry agentRegistry, IAgentSessionManage agentSessionManage) {
        super();
        this.agentRegistry = agentRegistry;
        this.agentSessionManage = agentSessionManage;
    }

    @Override
    public void invoke(IAgentEvent agentEvent) throws Exception {
        IAgentSession agentSession = agentEvent.getAgentSession();
        IAgent currentAgent = null;

        if (agentSession == null) {
            agentSession = agentSessionManage.createAndAddAgentSession();
            currentAgent = this.agentRegistry.getRouterAgent();
        } else {
            currentAgent = agentSessionManage.getCurrentSessionItem(agentSession).getAgent();
        }

        invoke(currentAgent, agentEvent);
    }

    @Override
    public void invoke(IAgent agent, IAgentEvent agentEvent) throws Exception {
        agent.init(new EventListener(agentEvent, agentSessionManage));
        agent.invoke(agentEvent);
    }

}
