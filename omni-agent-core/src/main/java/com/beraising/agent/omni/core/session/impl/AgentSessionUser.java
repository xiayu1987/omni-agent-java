package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.session.IAgentSessionUser;

public class AgentSessionUser implements IAgentSessionUser {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
