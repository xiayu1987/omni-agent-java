package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.impl.AgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public class AgentSession implements IAgentSession {

    private String agentSessionId;
    private String parentSessionId;
    private EUserType userType = EUserType.USER;
    private String userId;

    @JsonDeserialize(contentAs = AgentSessionItem.class)
    private List<IAgentSessionItem> agentSessionItems;

    @JsonDeserialize(contentAs = AgentRuntimeContext.class)
    private List<IAgentRuntimeContext> agentRuntimeContexts;

    public AgentSession() {
        super();
        this.agentSessionItems = new ArrayList<>();
        this.agentRuntimeContexts = new ArrayList<>();
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    @Override
    public IAgentRuntimeContext getCurrentRuntimeContext() {
        return ListUtils.lastOf(this.agentRuntimeContexts);
    }

}
