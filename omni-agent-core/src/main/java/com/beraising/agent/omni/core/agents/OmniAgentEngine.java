package com.beraising.agent.omni.core.agents;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.IAgentSessionUser;

@Component
public class OmniAgentEngine {

    private final OmniAgent omniAgent;
    private final IAgentSessionManage agentSessionManage;

    public OmniAgentEngine(OmniAgent omniAgent, IAgentSessionManage agentSessionManage) {
        super();
        this.omniAgent = omniAgent;
        this.agentSessionManage = agentSessionManage;
    }

    public void execute(String userQuery, IAgentSessionUser agentSessionUser) throws Exception {
        IAgentSession agentSession = agentSessionManage.createAndAddAgentSession(agentSessionUser);
        execute(userQuery, agentSession);
    }

    public void execute(String userQuery, IAgentSession agentSession) throws Exception {
        IAgent currentAgent = agentSessionManage.getCurrentSessionPart(agentSession).getAgent();
        if (currentAgent == null) {
            omniAgent.init(new AgentListener());
            omniAgent.invoke(userQuery,
                    agentSessionManage.getCurrentSessionPart(agentSession).getAgentRuntimeContext());
        } else {
            currentAgent.init(new AgentListener());
            currentAgent.invoke(userQuery,
                    agentSessionManage.getCurrentSessionPart(agentSession).getAgentRuntimeContext());
        }

    }

    public class AgentListener implements IAgentListener {

        public AgentListener() {
            super();
        }

        @Override
        public void beforeInvoke() throws Exception {

        }

        public void afterInvoke(IAgentRuntimeContext agentRuntimeContext) throws Exception {

        }

    }

}
