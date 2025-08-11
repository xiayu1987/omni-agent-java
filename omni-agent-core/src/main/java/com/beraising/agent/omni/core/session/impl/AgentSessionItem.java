package com.beraising.agent.omni.core.session.impl;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.session.IAgentSessionItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentSessionItem implements IAgentSessionItem {

    private IAgent agent;

}
