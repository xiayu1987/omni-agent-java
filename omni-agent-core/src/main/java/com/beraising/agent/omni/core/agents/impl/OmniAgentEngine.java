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

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.agents.intent.IIntentAgent;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.event.ISseChanel;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentRequest;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.impl.AgentSessionItem;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    public IAgentSessionManage getAgentSessionManage() {
        return this.agentSessionManage;
    }

    @Override
    public IAgentEvent invoke(IAgentEvent agentEvent) throws Exception {
        IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
        AgentContext ctx = new AgentContext();

        if (agentSession == null) {
            agentSession = eventListener.onStart(null, agentEvent);
        } else {
            ctx.lastContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            ctx.nextAgentName = agentSessionManage.getCurrentSessionItem(agentSession).getAgentName();
            eventListener.onStart(null, agentEvent);
        }

        IIntentAgent intentAgent = agentRegistry.getIntentAgent();
        IAgentEvent intentEvent = createIntentEvent(agentEvent);
        eventListener.onStart(agentSession, intentEvent);

        intentAgent.init(new EventListener(agentSessionManage, agentRuntimeContextBuilder) {
            @Override
            public void onComplete(IAgent agent,
                    IAgentEvent evt,
                    IAgentRuntimeContext runtimeContext,
                    IAgentResponse response) {
                super.onComplete(agent, evt, runtimeContext, response);
                handleIntentComplete(intentEvent, agentEvent, ctx.nextAgentName, ctx.lastContext);
            }
        });

        intentAgent.invoke(intentEvent);
        return agentEvent;
    }

    private void handleIntentComplete(IAgentEvent intentEvent,
            IAgentEvent userEvent,
            String nextAgentName,
            IAgentRuntimeContext userContext) {
        try {
            JsonObject obj = JsonParser.parseString(intentEvent.getAgentResponse().getResponseData())
                    .getAsJsonObject();
            String nextIntentAgentName = obj.get("nextAgentName").getAsString();

            if (!Objects.equals(nextAgentName, nextIntentAgentName) && userContext != null) {
                userContext.setIsEnd(true);
            }

            IAgent nextAgent = agentRegistry.getAgentByName(nextIntentAgentName);
            nextAgent.init(eventListener);
            nextAgent.invoke(userEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IAgentEvent createIntentEvent(IAgentEvent userEvent) {

        IAgentSession userSession = agentSessionManage
                .getAgentSessionById(userEvent.getAgentSessionId());

        IAgentRuntimeContext latestAgentRuntimeContext = null;

        if (userSession == null) {

        } else {
            latestAgentRuntimeContext = ListUtils.lastOf(userSession.getAgentRuntimeContexts());
        }

        String chatContext = "";
        if (latestAgentRuntimeContext != null && latestAgentRuntimeContext.isEnd() == false) {

            chatContext = latestAgentRuntimeContext.getAgentEvents().stream()
                    .map((item) -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("\r\n请求 request:\r\n");
                        sb.append(item.getAgentRequest().getRequestData());
                        sb.append("\r\n响应 response:\r\n");
                        sb.append(item.getAgentResponse().getResponseData());

                        return sb.toString();
                    })
                    .collect(Collectors.joining(", "));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\r\n之前聊天记录 chatContext:\r\n");
        sb.append(chatContext);
        sb.append("\r\n当前最新请求 request:\r\n");
        sb.append(userEvent.getAgentRequest().getRequestData());

        return AgentEvent.builder().userType(EUserType.SYSTEM)
                .isStream(userEvent.isStream())
                // .sseChanel(userEvent.getSseChanel())
                .agentSessionId("")
                .agentRequest(
                        AgentRequest.builder().requestType(EAgentRequestType.TEXT)
                                .requestData(sb.toString()).build())
                .build();
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

                IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());

                endSession(agent, agentEvent, agentResponse, agentSession);
            }

        }

        @Override
        public IAgentSession onStart(IAgentSession parentSession, IAgentEvent agentEvent) {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());

            if (agentSession == null) {
                String parentSessionId = null;
                if (parentSession != null) {
                    parentSessionId = parentSession.getAgentSessionId();
                }
                agentSession = agentSessionManage.createAgentSession(parentSessionId,
                        agentEvent.getUserType(), agentEvent.getUserId());
            }

            agentEvent.setAgentSessionId(agentSession.getAgentSessionId());

            return agentSession;

        }

        @Override
        public void onInvokeStream(IAgent agent, IAgentEvent agentEvent,
                IAgentRuntimeContext agentRuntimeContext, AgentGraphInvokeStreamContent content) {

            ISseChanel sseChanel = agentEvent.getSseChanel();

            if (agentRuntimeContext.isEnd()) {
                return;
            }

            if (content.isError()) {
                onError(agent, agentEvent, agentRuntimeContext, new Exception(content.getContent()));
                return;
            } else if (!content.isComplete()) {

                agentEvent.setAgentResponse(AgentResponse.builder().responseType(EAgentResponseType.TEXT)
                        .responseData(content.getContent()).build());
                if (sseChanel != null) {
                    sseChanel
                            .tryEmitNext(agentEvent);
                }

                return;
            }

        }

        @Override
        public IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph) {
            IAgentRuntimeContext result = null;

            try {
                IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
                IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());

                if (lastAgentRuntimeContext == null || lastAgentRuntimeContext.isEnd()
                        || !agent.getName().equals(lastAgentRuntimeContext.getAgent().getName())
                        || lastAgentRuntimeContext.getCompiledGraph() == null) {

                    result = agentRuntimeContextBuilder.build(agentEvent, agentGraph);

                    agentSessionManage.addAgentRuntimeContext(agentSession, result);

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

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            if (lastAgentRuntimeContext.getAgentRuntimeContextId()
                    .equals(agentRuntimeContext.getAgentRuntimeContextId()) && !lastAgentRuntimeContext.isEnd()) {
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

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            IAgentRuntimeContext lastAgentRuntimeContext = ListUtils.lastOf(agentSession.getAgentRuntimeContexts());
            if (lastAgentRuntimeContext.getAgentRuntimeContextId()
                    .equals(agentRuntimeContext.getAgentRuntimeContextId()) && !lastAgentRuntimeContext.isEnd()) {
                IAgentEvent lastAgentEvent = ListUtils.lastOf(lastAgentRuntimeContext.getAgentEvents());
                lastAgentEvent.setAgentResponse(agentResponse);

                endSession(agent, agentEvent, agentResponse, agentSession);
            }
        }

        private void endSession(IAgent agent, IAgentEvent agentEvent, IAgentResponse agentResponse,
                IAgentSession agentSession) {
            this.agentSessionManage.addSessionItem(agentSession, AgentSessionItem.builder()
                    .agentName(agent.getName())
                    .agentRequest(agentEvent.getAgentRequest())
                    .agentResponse(null)
                    .build());

            this.agentSessionManage.addSessionItem(agentSession, AgentSessionItem.builder()
                    .agentName(agent.getName())
                    .agentRequest(null)
                    .agentResponse(agentEvent.getAgentResponse())
                    .build());

            ISseChanel sseChanel = agentEvent.getSseChanel();
            if (sseChanel != null
                    && (agentSession.getParentSessionId() == null || agentSession.getParentSessionId().isEmpty())) {
                sseChanel.tryEmitComplete();
            }
        }

    }

    class AgentContext {
        String nextAgentName;
        IAgentRuntimeContext lastContext;
    }

}
