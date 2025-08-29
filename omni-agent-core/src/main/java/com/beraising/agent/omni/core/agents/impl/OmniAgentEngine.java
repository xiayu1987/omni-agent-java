/*
 * Copyright 2025 重庆北睿星科技有限公司 (www.beraising.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beraising.agent.omni.core.agents.impl;

import org.springframework.http.codec.ServerSentEvent;
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

import reactor.core.publisher.Sinks;

@Component
public class OmniAgentEngine implements IAgentEngine {

    private final AgentRegistry agentRegistry;
    private final IAgentSessionManage agentSessionManage;
    private final IAgentRuntimeContextBuilder agentRuntimeContextBuilder;
    private IEventListener eventListener;

    public OmniAgentEngine(AgentRegistry agentRegistry, IAgentSessionManage agentSessionManage,
            IAgentRuntimeContextBuilder agentRuntimeContextBuilder) {
        super();
        this.agentRegistry = agentRegistry;
        this.agentSessionManage = agentSessionManage;
        this.agentRuntimeContextBuilder = agentRuntimeContextBuilder;
        this.eventListener = new EventListener(this.agentSessionManage, this.agentRuntimeContextBuilder);
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

            if (agentRuntimeContext != null) {
                agentRuntimeContext.setIsEnd(true);
            }

            if (agentEvent != null) {

                IAgentResponse agentResponse = AgentResponse.builder().responseType(EAgentResponseType.ERROR)
                        .responseData(throwable.getMessage()).build();

                agentEvent.setAgentResponse(agentResponse);

                IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

                endSession(agent, agentEvent, agentResponse, agentSession);
            }

        }

        @Override
        public IAgentSession onStart(IAgent agent, IAgentEvent agentEvent) {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());

            if (agentSession == null) {
                agentSession = agentSessionManage.createAgentSession();
                agentSessionManage.addSession(agentSession);
            }

            agentEvent.setAgentSessionID(agentSession.getAgentSessionId());

            return agentSession;

        }

        @Override
        public void onInvokeStream(IAgent agent, IAgentEvent agentEvent,
                IAgentRuntimeContext agentRuntimeContext, AgentGraphInvokeStreamContent content) {

            Sinks.Many<ServerSentEvent<IAgentEvent>> sseChanel = agentEvent.getSseChanel();

            if (sseChanel == null || agentRuntimeContext.isEnd()) {
                return;
            }

            if (content.isError()) {
                onError(agent, agentEvent, agentRuntimeContext, new Exception(content.getContent()));
                return;
            } else if (!content.isComplete()) {

                agentEvent.setAgentResponse(AgentResponse.builder().responseType(EAgentResponseType.TEXT)
                        .responseData(content.getContent()).build());
                sseChanel
                        .tryEmitNext(ServerSentEvent.<IAgentEvent>builder().data(agentEvent).build());
                return;
            }

        }

        @Override
        public IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph) {
            IAgentRuntimeContext result = null;

            try {
                IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());
                IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());

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

            } catch (Exception e) {
                onError(agent, agentEvent, result, e);
            }

            return result;
        }

        @Override
        public void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) {
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

                endSession(agent, agentEvent, agentResponse, agentSession);
            }
        }

        @Override
        public void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) {
            if (agentRuntimeContext.isEnd()) {
                return;
            }

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionID());
            IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            if (lastAgentRuntimeContext.getAgentRuntimeContextID()
                    .equals(agentRuntimeContext.getAgentRuntimeContextID()) && !lastAgentRuntimeContext.isEnd()) {
                IAgentEvent lastAgentEvent = ListUtils.lastOf(lastAgentRuntimeContext.getAgentEvents());
                lastAgentEvent.setAgentResponse(agentResponse);

                endSession(agent, agentEvent, agentResponse, agentSession);
            }
        }

        private void endSession(IAgent agent, IAgentEvent agentEvent, IAgentResponse agentResponse,
                IAgentSession agentSession) {
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

            Sinks.Many<ServerSentEvent<IAgentEvent>> sseChanel = agentEvent.getSseChanel();
            if (sseChanel != null) {
                sseChanel.tryEmitComplete();
                agentEvent.setSseChanel(null);
            }
        }

    }

}
