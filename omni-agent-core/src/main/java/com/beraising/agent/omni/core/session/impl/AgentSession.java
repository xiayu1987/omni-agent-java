package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

import lombok.Data;

@Data
public class AgentSession implements IAgentSession {

    private String agentSessionId;
    private String parentSessionId;
    private EUserType userType = EUserType.USER;
    private String userId;
    private List<IAgentSessionItem> agentSessionItems;
    private List<IAgentRuntimeContext> agentRuntimeContexts;

    public AgentSession() {
        super();
        this.agentSessionItems = new ArrayList<>();
        this.agentRuntimeContexts = new ArrayList<>();
    }

}
