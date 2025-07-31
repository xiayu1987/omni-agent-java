package com.beraising.agent.omni.core.agents;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.impl.AgentSession;

@Component
public class OmniAgentLauncher {

    private final OmniAgent omniAgent;
    private final IAgentSessionManage agentSessionManage;

    public OmniAgentLauncher(OmniAgent omniAgent, IAgentSessionManage agentSessionManage) {
        super();
        this.omniAgent = omniAgent;
        this.agentSessionManage = agentSessionManage;
    }

    public void launch() throws Exception {
        // omniAgent.init();
        // omniAgent.invoke();
    }

}
