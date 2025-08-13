package com.beraising.agent.omni.core.agents.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.impl.AgentSessionItem;
import com.google.gson.Gson;

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
            String currentAgentName = agentSessionManage.getCurrentSessionItem(agentSession).getAgentName();

            currentAgent = this.agentRegistry.getAgentByName(currentAgentName);
        }

        return invoke(currentAgent, agentEvent);
    }

    @Override
    public IAgentEvent invoke(IAgent agent, IAgentEvent agentEvent) throws Exception {

        eventListener.onStart(agent, agentEvent);

        agent.init(eventListener);
        IAgentEvent result = agent.invoke(agentEvent);

        IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

        System.out.println("Agent Session Items: " + new Gson().toJson(agentSession.getAgentSessionItems()));

        return result;
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
        public void onError(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                Throwable throwable) {
            if (agentEvent != null) {

                IAgentResponse agentResponse = AgentResponse.builder().responseType(EAgentResponseType.ERROR)
                        .responseData(throwable.getMessage()).build();

                agentEvent.setAgentResponse(agentResponse);

                IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

                agentSession.getAgentSessionItems().add(AgentSessionItem.builder()
                        .agentName(agent.getName())
                        .agentRequest(agentEvent.getAgentRequest())
                        .agentResponse(null)
                        .build());

                agentSession.getAgentSessionItems().add(AgentSessionItem.builder()
                        .agentName(agent.getName())
                        .agentRequest(null)
                        .agentResponse(agentResponse)
                        .build());
            }

            if (agentRuntimeContext != null) {
                agentRuntimeContext.setIsEnd(true);
            }
        }

        @Override
        public IAgentSession onStart(IAgent agent, IAgentEvent agentEvent) throws Exception {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

            if (agentSession == null) {
                agentSession = agentSessionManage.createAgentSession();
                agentSessionManage.addSession(agentSession);
            }

            agentEvent.setAgentSessionID(agentSession.getAgentSessionId());

            return agentSession;

        }

        @Override
        public IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph)
                throws Exception {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());
            IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            IAgentRuntimeContext result = null;

            if (lastAgentRuntimeContext == null || lastAgentRuntimeContext.isEnd()
                    || !agent.getName().equals(lastAgentRuntimeContext.getAgent().getName())
                    || lastAgentRuntimeContext.getCompiledGraph() == null) {
                result = agentRuntimeContextBuilder.build(agentEvent, agentGraph);
                agentSessionManage.addAgentRuntimeContext(result);

                if (lastAgentRuntimeContext != null) {
                    lastAgentRuntimeContext.setIsEnd(true);
                }
            } else {
                result = lastAgentRuntimeContext;
                result.getAgentEvents().add(agentEvent);
            }

            return result;
        }

        @Override
        public void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) throws Exception {
            if (agentRuntimeContext.isEnd()) {
                return;
            }

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());
            IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            if (lastAgentRuntimeContext.getAgentRuntimeContextID()
                    .equals(agentRuntimeContext.getAgentRuntimeContextID()) && !lastAgentRuntimeContext.isEnd()) {
                lastAgentRuntimeContext.setIsEnd(true);
                IAgentEvent lastAgentEvent = ListUtils.lastOf(lastAgentRuntimeContext.getAgentEvents());
                lastAgentEvent.setAgentResponse(agentResponse);

                agentSession.getAgentSessionItems().add(AgentSessionItem.builder()
                        .agentName(agent.getName())
                        .agentRequest(agentEvent.getAgentRequest())
                        .agentResponse(null)
                        .build());

                agentSession.getAgentSessionItems().add(AgentSessionItem.builder()
                        .agentName(agent.getName())
                        .agentRequest(null)
                        .agentResponse(agentResponse)
                        .build());
            }
        }

        @Override
        public void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) throws Exception {
            if (agentRuntimeContext.isEnd()) {
                return;
            }

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());
            IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            if (lastAgentRuntimeContext.getAgentRuntimeContextID()
                    .equals(agentRuntimeContext.getAgentRuntimeContextID()) && !lastAgentRuntimeContext.isEnd()) {
                IAgentEvent lastAgentEvent = ListUtils.lastOf(lastAgentRuntimeContext.getAgentEvents());
                lastAgentEvent.setAgentResponse(agentResponse);

                agentSession.getAgentSessionItems().add(AgentSessionItem.builder()
                        .agentName(agent.getName())
                        .agentRequest(agentEvent.getAgentRequest())
                        .agentResponse(null)
                        .build());

                agentSession.getAgentSessionItems().add(AgentSessionItem.builder()
                        .agentName(agent.getName())
                        .agentRequest(null)
                        .agentResponse(agentResponse)
                        .build());
            }
        }
    }
}
