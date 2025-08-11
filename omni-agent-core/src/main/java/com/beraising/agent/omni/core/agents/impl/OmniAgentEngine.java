package com.beraising.agent.omni.core.agents.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.impl.AgentSessionItem;

@Component
public class OmniAgentEngine implements IAgentEngine {

    private final AgentRegistry agentRegistry;
    private final IAgentSessionManage agentSessionManage;
    private IEventListener eventListener;

    public OmniAgentEngine(AgentRegistry agentRegistry, IAgentSessionManage agentSessionManage,
            IAgentRuntimeContextBuilder agentRuntimeContextBuilder) {
        super();
        this.agentRegistry = agentRegistry;
        this.agentSessionManage = agentSessionManage;
        this.eventListener = new EventListener(agentSessionManage, agentRuntimeContextBuilder);
    }

    @Override
    public IAgentEvent invoke(IAgentEvent agentEvent) throws Exception {
        IAgentSession agentSession = agentSessionManage
                .getAgentSessionById(agentEvent.getAgentSessionID());
        IAgent currentAgent = null;

        if (agentSession == null) {

            currentAgent = this.agentRegistry.getRouterAgent();
        } else {
            currentAgent = agentSessionManage.getCurrentSessionItem(agentSession).getAgent();
        }

        return invoke(currentAgent, agentEvent);
    }

    @Override
    public IAgentEvent invoke(IAgent agent, IAgentEvent agentEvent) throws Exception {

        eventListener.onStart(agent, agentEvent);

        agent.init(eventListener);
        return agent.invoke(agentEvent);
    }

    public class EventListener implements IEventListener {
        private IAgentSessionManage agentSessionManage;
        private IAgentRuntimeContextBuilder agentRuntimeContextBuilder;

        public EventListener(IAgentSessionManage agentSessionManage,
                IAgentRuntimeContextBuilder agentRuntimeContextBuilder) {
            super();
            this.agentSessionManage = agentSessionManage;
            this.agentRuntimeContextBuilder = agentRuntimeContextBuilder;
        }

        @Override
        public IAgentSession onStart(IAgent agent, IAgentEvent agentEvent) throws Exception {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

            if (agentSession == null) {
                agentSession = agentSessionManage.createAgentSession();
                agentSession.setParentAgentSessionId(agentEvent.getParentAgentSessionID());
                agentSessionManage.addSession(agentSession);
            }

            IAgentSessionItem agentSessionItem = new AgentSessionItem();
            agentSessionItem.setAgent(agent);
            agentSession.getAgentSessionItems().add(agentSessionItem);

            agentEvent.setAgentSessionID(agentSession.getAgentSessionId());
            agentEvent.setParentAgentSessionID(agentSession.getParentAgentSessionId());

            return agentSession;

        }

        @Override
        public IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph)
                throws Exception {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());
            IAgentRuntimeContext agentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());

            if (agentRuntimeContext == null || agentRuntimeContext.isEnd()
                    || !agent.getName().equals(agentRuntimeContext.getAgent().getName())) {
                agentRuntimeContext = agentRuntimeContextBuilder.build(agentEvent, agentGraph);
                agentSessionManage.addAgentRuntimeContext(agentRuntimeContext);

                if (agentRuntimeContext != null) {
                    agentRuntimeContext.setIsEnd(true);
                }
            }

            return agentRuntimeContext;
        }

        @Override
        public void beforeGraphInvoke(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext)
                throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeGraphNodeInvoke(IAgent agent, IAgentEvent agentEvent,
                IAgentRuntimeContext agentRuntimeContext) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterGraphNodeInvoke(IAgent agent, IAgentEvent agentEvent,
                IAgentRuntimeContext agentRuntimeContext) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void onComplete(IAgent agent, IAgentSession agentSession)
                throws Exception {
            IAgentRuntimeContext agentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            if (agentRuntimeContext.getAgent().getName().equals(agent.getName())) {
                agentRuntimeContext.setIsEnd(true);
                IAgentEvent agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());
                agentEvent.setAgentResponse(null);
            }
        }
    }
}
