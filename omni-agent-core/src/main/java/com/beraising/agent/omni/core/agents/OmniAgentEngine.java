package com.beraising.agent.omni.core.agents;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.IAgentSessionUser;

@Component
public class OmniAgentEngine implements IAgentListener {

    private final OmniAgent omniAgent;
    private final IAgentSessionManage agentSessionManage;

    public OmniAgentEngine(OmniAgent omniAgent, IAgentSessionManage agentSessionManage) {
        super();
        this.omniAgent = omniAgent;
        this.agentSessionManage = agentSessionManage;
    }

    public void execute(IAgentSessionUser agentSessionUser) throws Exception {
        IAgentSession agentSession = agentSessionManage.createAndAddAgentSession(agentSessionUser);
        execute(agentSession);
    }

    public void execute(IAgentSession agentSession) throws Exception {
        IAgent currentAgent = agentSessionManage.getCurrentSessionPart(agentSession).getAgent();
        if (currentAgent == null) {
            omniAgent.init(this);
            omniAgent.invoke(agentSessionManage.getCurrentSessionPart(agentSession).getAgentRuntimeContext());
        } else {
            currentAgent.init(this);
            currentAgent.invoke(agentSessionManage.getCurrentSessionPart(agentSession).getAgentRuntimeContext());
        }

    }

    @Override
    public void beforeInvoke() throws Exception {

    }

    @Override
    public void afterInvoke() throws Exception {

    }

}
