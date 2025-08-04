package com.beraising.agent.omni.core.agents.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.agents.IAgentListener;
import com.beraising.agent.omni.core.agents.IAgentRequest;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
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

    public void invoke(IAgentRequest agentRequest) throws Exception {
        IAgentSession agentSession = agentSessionManage.createAndAddAgentSession();
        invoke(agentRequest, agentSession);
    }

    public void invoke(IAgentRequest agentRequest, IAgentSession agentSession) throws Exception {
        IAgentSessionItem agentSessionItem = agentSessionManage.getCurrentSessionItem(agentSession);
        IAgent currentAgent = agentSessionItem == null ? this.agentRegistry.getRouterAgent()
                : agentSessionItem.getAgent();

        currentAgent.init(new AgentListener(agentSession));
        if (agentSessionItem == null) {
            currentAgent.invoke(agentRequest);
        } else {
            currentAgent.invoke(agentRequest, agentSessionItem.getAgentRuntimeContext());
        }

    }

    @Override
    public void invoke(IAgent agent, IAgentRequest agentRequest) throws Exception {
        IAgentSession agentSession = agentSessionManage.createAndAddAgentSession();
        invoke(agent, agentRequest, agentSession);
    }

    @Override
    public void invoke(IAgent agent, IAgentRequest agentRequest, IAgentSession agentSession) throws Exception {
        agent.init(new AgentListener(agentSession));
        agent.invoke(agentRequest);
    }

    public class AgentListener implements IAgentListener {

        private IAgentSession agentSession;

        public AgentListener(IAgentSession agentSession) {
            super();
            this.agentSession = agentSession;
        }

        @Override
        public void beforeInvoke(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext)
                throws Exception {
        }

        @Override
        public void afterInvoke(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext) throws Exception {
        }

    }

}
