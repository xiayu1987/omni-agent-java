package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionPart;

@Component
public class AgentSession implements IAgentSession {

    private List<IAgentSessionPart> agentSessionPart;

    public AgentSession() {
        super();
        agentSessionPart = new ArrayList<>();
    }

    public List<IAgentSessionPart> getAgentSessionPart() {
        return agentSessionPart;
    }

    public void addAgentSessionPart(IAgentSessionPart agentSessionPart) {
        this.agentSessionPart.add(agentSessionPart);
    }

}
