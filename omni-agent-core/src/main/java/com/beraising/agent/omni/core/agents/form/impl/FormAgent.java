package com.beraising.agent.omni.core.agents.form.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentBase;
import com.beraising.agent.omni.core.agents.AgentContext;
import com.beraising.agent.omni.core.agents.form.IFormAgent;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.form.IFormGraph;

@Component
public class FormAgent extends AgentBase implements IFormAgent {

    private final IFormGraph formGraph;
    private final AgentContext agentContext;

    public FormAgent(AgentContext agentContext, IFormGraph formGraph) {
        this.formGraph = formGraph;
        this.agentContext = agentContext;
    }

    @Override
    public String getName() {
        return "Form Agent";
    }

    @Override
    public String getDescription() {
        return "处理表单的提交，包含请假、出差等功能。";
    }

    @Override
    public IGraph getGraph() {
        return this.formGraph;
    }

    @Override
    public AgentContext getAgentContext() {
        return this.agentContext;
    }

}
