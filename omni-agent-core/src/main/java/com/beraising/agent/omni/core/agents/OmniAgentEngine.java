package com.beraising.agent.omni.core.agents;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

@Component
public class OmniAgentEngine {

    private final OmniAgent omniAgent;
    private final IAgentSessionManage agentSessionManage;

    public OmniAgentEngine(OmniAgent omniAgent, IAgentSessionManage agentSessionManage) {
        super();
        this.omniAgent = omniAgent;
        this.agentSessionManage = agentSessionManage;
    }

    public void invoke(IAgentRequest agentRequest) throws Exception {
        IAgentSession agentSession = agentSessionManage.createAndAddAgentSession();
        invoke(agentRequest, agentSession);
    }

    public void invoke(IAgentRequest agentRequest, IAgentSession agentSession) throws Exception {
        IAgentSessionItem agentSessionItem = agentSessionManage.getCurrentSessionItem(agentSession);
        IAgent currentAgent = agentSessionItem == null ? omniAgent : agentSessionItem.getAgent();

        currentAgent.init(new AgentListener(agentSession));
        if (agentSessionItem == null) {
            currentAgent.invoke(agentRequest);
        } else {
            currentAgent.invoke(agentRequest, agentSessionItem.getAgentRuntimeContext());
        }

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
