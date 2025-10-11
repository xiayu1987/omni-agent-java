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
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.event.ISseChanel;
import com.beraising.agent.omni.core.event.IEventListener.StreamContent;
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
    private IntentEventListener intentEventListener;

    public OmniAgentEngine(AgentRegistry agentRegistry, IAgentSessionManage agentSessionManage,
            IAgentRuntimeContextBuilder agentRuntimeContextBuilder) {
        super();
        this.agentRegistry = agentRegistry;
        this.agentSessionManage = agentSessionManage;
        this.agentRuntimeContextBuilder = agentRuntimeContextBuilder;
        this.eventListener = new EventListener(this.agentSessionManage, this.agentRuntimeContextBuilder);
        this.intentEventListener = new IntentEventListener(this.agentSessionManage, this.agentRuntimeContextBuilder,
                (agent, agentEvent, agentRuntimeContext,
                        agentResponse) -> {
                    handleIntentComplete(agent, agentEvent, agentRuntimeContext,
                            agentResponse);
                }, (agent, agentEvent, agentRuntimeContext,
                        throwable) -> {
                    handleIntentError(agent, agentEvent, agentRuntimeContext,
                            throwable);
                });
    }

    public IAgentSessionManage getAgentSessionManage() {
        return this.agentSessionManage;
    }

    @Override
    public IAgentEvent invoke(IAgentEvent agentEvent) throws Exception {
        IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());

        if (agentSession == null) {
            agentSession = eventListener.onStart(null, agentEvent);
        } else {
            eventListener.onStart(null, agentEvent);
        }

        IIntentAgent intentAgent = agentRegistry.getIntentAgent();
        IAgentEvent intentEvent = createIntentEvent(agentEvent);

        intentEventListener.onStart(agentSession, intentEvent);

        intentAgent.init(intentEventListener);

        intentAgent.invoke(intentEvent);
        return agentEvent;
    }

    private void handleIntentError(IAgent intentAgent, IAgentEvent intentEvent,
            IAgentRuntimeContext intentAgentRuntimeContext,
            Throwable throwable) {
        IAgentEvent userCurrentEvent = null;
        IAgentSession userAgentSession = agentSessionManage.getAgentSessionById(intentEvent.getParentAgentSessionId());
        IAgentRuntimeContext userCurrentRuntimeContext = null;

        if (userAgentSession == null) {
            return;
        }

        userCurrentRuntimeContext = userAgentSession.getCurrentRuntimeContext();

        if (userCurrentRuntimeContext == null) {
            return;
        }

        userCurrentEvent = userCurrentRuntimeContext.getCurrentEvent();

        if (userCurrentEvent == null) {
            return;
        }

        eventListener.onCollab(intentAgent, userCurrentEvent, intentEvent);

        eventListener.onError(intentAgent, userCurrentEvent, userCurrentRuntimeContext, throwable);
    }

    private void handleIntentComplete(IAgent intentAgent,
            IAgentEvent intentEvent,
            IAgentRuntimeContext intentAgentRuntimeContext,
            IAgentResponse intentAgentResponse) {
        IAgentEvent userCurrentEvent = null;
        IAgentSession userAgentSession = agentSessionManage.getAgentSessionById(intentEvent.getParentAgentSessionId());
        IAgentRuntimeContext userCurrentRuntimeContext = null;
        String userNextAgentName = "";

        if (userAgentSession == null) {
            return;
        }

        userCurrentRuntimeContext = userAgentSession.getCurrentRuntimeContext();

        if (userCurrentRuntimeContext == null) {
            return;
        }

        userCurrentEvent = userCurrentRuntimeContext.getCurrentEvent();

        if (userCurrentEvent == null) {
            return;
        }

        eventListener.onCollab(intentAgent, userCurrentEvent, intentEvent);

        try {
            JsonObject obj = JsonParser.parseString(intentEvent.getAgentResponse().getResponseData())
                    .getAsJsonObject();

            boolean isSuccess = obj.has("isSuccess") && obj.get("isSuccess").getAsBoolean();
            if (!isSuccess) {
                // 错误情况
                String errMsg = obj.has("message") ? obj.get("message").getAsString() : "意图识别失败";
                eventListener.onError(intentAgent, userCurrentEvent, null, new Exception(errMsg));
                return;
            }

            boolean isContinue = obj.has("isContinue") && obj.get("isContinue").getAsBoolean();
            boolean isAmbiguous = obj.has("isAmbiguous") && obj.get("isAmbiguous").getAsBoolean();
            String nextIntentAgentName = obj.has("nextAgentName") ? obj.get("nextAgentName").getAsString() : "";

            // 情况 1: 模糊意图
            if (isAmbiguous) {
                // 这里不要直接切换或结束，而是提示用户确认
                String msg = obj.has("message") ? obj.get("message").getAsString() : "意图不明确，请确认是否继续当前任务";

                eventListener.onInvokeStream(intentAgent, userCurrentEvent, userCurrentRuntimeContext,
                        StreamContent.builder().isError(false).content(msg).isComplete(false).build());

                eventListener.onInterrupt(intentAgent, userCurrentEvent, userCurrentRuntimeContext,
                        AgentResponse.builder().responseType(EAgentResponseType.TEXT).responseData(msg).build());
                return;
            }

            // 情况 2: 意图没变，继续上一个任务
            if (isContinue && Objects.equals(userNextAgentName, nextIntentAgentName)) {
                if (userCurrentRuntimeContext != null) {
                    // 复用上一个 agent
                    IAgent nextAgent = agentRegistry.getAgentByName(userNextAgentName);
                    if (nextAgent != null) {

                        nextAgent.init(eventListener);
                        nextAgent.invoke(userCurrentEvent);
                    }
                }
                return;
            }

            IAgent nextAgent = agentRegistry.getAgentByName(nextIntentAgentName);
            // 情况 3: 意图改变 -> 结束上一个任务
            if (!isContinue && userCurrentRuntimeContext != null
                    && userCurrentRuntimeContext.getGraphRunStatus() >= 1) {

                if (nextAgent == null) {
                    eventListener.onError(intentAgent, userCurrentEvent, userCurrentRuntimeContext,
                            new Exception("无法识别意图"));
                    return;
                } else {
                    eventListener.onEndGraph(userCurrentRuntimeContext.getAgent(), userCurrentEvent,
                            userCurrentRuntimeContext);
                }
            }

            if (nextAgent == null) {
                eventListener.onError(intentAgent, userCurrentEvent, userCurrentRuntimeContext,
                        new Exception("无法识别意图"));
            } else {

                nextAgent.init(eventListener);
                nextAgent.invoke(userCurrentEvent);
            }

        } catch (

        Exception e) {
            eventListener.onError(intentAgent, userCurrentEvent, userCurrentRuntimeContext,
                    new Exception(e.getMessage()));
        }
    }

    private IAgentEvent createIntentEvent(IAgentEvent userEvent) {

        // 获取用户会话和当前运行上下文
        IAgentSession userSession = agentSessionManage.getAgentSessionById(userEvent.getAgentSessionId());
        IAgentRuntimeContext userCurrentRuntimeContext = (userSession != null)
                ? userSession.getCurrentRuntimeContext()
                : null;

        // 构建历史上下文
        String chatContext = "";
        if (userCurrentRuntimeContext != null && !userCurrentRuntimeContext.isEnd()) {
            chatContext = userCurrentRuntimeContext.getAgentEvents().stream()
                    .map(this::formatEventLog)
                    .collect(Collectors.joining("\n\n"));
        }

        // 构建 prompt
        StringBuilder promptBuilder = new StringBuilder();

        // 系统说明
        promptBuilder.append("【系统说明】：\n")
                .append("你是意图识别助理，需要根据用户输入和历史对话生成下一步意图事件。\n")
                .append("请严格按照上下文处理，不要输出多余文本或解释。\n\n");

        // 历史上下文
        if (!chatContext.isEmpty()) {
            promptBuilder.append("【之前聊天记录 chatContext】：\n")
                    .append(chatContext)
                    .append("\n\n");
        }

        // 当前请求
        promptBuilder.append("【当前最新请求 request】：\n")
                .append(userEvent.getAgentRequest().getRequestData());

        String promptContent = promptBuilder.toString();

        // 构建结果事件
        return AgentEvent.builder()
                .userType(EUserType.SYSTEM)
                .isStream(userEvent.isStream())
                .agentSessionId("") // 新的 session，置空
                .parentAgentSessionId(userEvent.getAgentSessionId())
                .agentRequest(AgentRequest.builder()
                        .requestType(EAgentRequestType.TEXT)
                        .requestData(promptContent)
                        .build())
                .build();
    }

    /**
     * 格式化单个事件日志
     */
    private String formatEventLog(IAgentEvent event) {
        StringBuilder sb = new StringBuilder();
        if (event.getAgentRequest() != null) {
            sb.append("请求:\n")
                    .append(event.getAgentRequest().getRequestData());
        }
        if (event.getAgentResponse() != null) {
            sb.append("\n响应:\n")
                    .append(event.getAgentResponse().getResponseData());
        }
        return sb.toString();
    }

    public class IntentEventListener extends EventListener {

        private IIntentComplete intentComplete;
        private IIntentError intentError;

        public IntentEventListener(IAgentSessionManage agentSessionManage,
                IAgentRuntimeContextBuilder agentRuntimeContextBuilder, IIntentComplete intentComplete,
                IIntentError intentError) {
            super(agentSessionManage, agentRuntimeContextBuilder);
            this.intentComplete = intentComplete;
            this.intentError = intentError;
        }

        @Override
        public void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) {
            super.onComplete(agent, agentEvent, agentRuntimeContext, agentResponse);

            intentComplete.exec(agent, agentEvent, agentRuntimeContext, agentResponse);
        }

        @Override
        public void onError(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                Throwable throwable) {

            super.onError(agent, agentEvent, agentRuntimeContext, throwable);

            intentError.exec(agent, agentEvent, agentRuntimeContext, throwable);
        }

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

                ISseChanel sseChanel = agentEvent.getSseChanel();

                if (sseChanel != null) {
                    sseChanel.tryEmitNext(agentEvent);
                }

                endSession(agent, agentEvent, agentResponse, agentSession);
            }

        }

        @Override
        public IAgentSession onStart(IAgentSession parentSession, IAgentEvent agentEvent) throws Exception {

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());

            if (agentSession == null) {
                String parentSessionId = null;
                if (parentSession != null) {
                    parentSessionId = parentSession.getAgentSessionId();
                }
                agentSession = agentSessionManage.createAgentSession(parentSessionId,
                        agentEvent.getUserType(), agentEvent.getUserId());

                agentEvent.setAgentSessionId(agentSession.getAgentSessionId());

                IAgentRuntimeContext runtimeContext = agentRuntimeContextBuilder.initialize(agentEvent);

                agentSessionManage.addAgentRuntimeContext(agentSession, runtimeContext);
            } else {

                IAgentRuntimeContext currentRuntimeContext = agentSession.getCurrentRuntimeContext();

                if (currentRuntimeContext != null) {
                    if (currentRuntimeContext.isEnd()) {
                        IAgentRuntimeContext runtimeContext = agentRuntimeContextBuilder.initialize(agentEvent);
                        agentSessionManage.addAgentRuntimeContext(agentSession, runtimeContext);
                    } else {
                        currentRuntimeContext.getAgentEvents().add(agentEvent);
                        agentSessionManage.updateAgentRuntimeContext(agentSession, currentRuntimeContext);
                    }

                }

            }

            return agentSession;

        }

        @Override
        public void onCollab(IAgent agent, IAgentEvent userEvent, IAgentEvent collabEvent) {

            IAgentSession userSession = agentSessionManage.getAgentSessionById(userEvent.getAgentSessionId());
            IAgentRuntimeContext userCurrentRuntimeContext = userSession.getCurrentRuntimeContext();

            if (userCurrentRuntimeContext != null) {

                IAgentEvent copyEvent = collabEvent.copy();
                copyEvent.setAgentRequest(userEvent.getAgentRequest());
                copyEvent.setAgentSessionId(userEvent.getAgentSessionId());
                copyEvent.setParentAgentSessionId(userEvent.getParentAgentSessionId());
                copyEvent.setUserId(userEvent.getUserId());
                copyEvent.setUserType(userEvent.getUserType());
                copyEvent.setSseChanel(userEvent.getSseChanel());

                userCurrentRuntimeContext.getAgentEvents().add(copyEvent);
            }

            agentSessionManage.updateAgentRuntimeContext(userSession, userCurrentRuntimeContext);
        }

        @Override
        public IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph)
                throws Exception {
            IAgentRuntimeContext result = null;
            IAgentSession agentSession = null;

            agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            IAgentRuntimeContext currentRuntimeContext = agentSession.getCurrentRuntimeContext();

            if (currentRuntimeContext == null) {
                result = agentRuntimeContextBuilder.build(agentEvent, agentGraph);

                agentSessionManage.addAgentRuntimeContext(agentSession, result);

                return result;
            }

            if (currentRuntimeContext.isEnd()) {
                result = agentRuntimeContextBuilder.build(agentEvent, agentGraph);

                agentSessionManage.addAgentRuntimeContext(agentSession, result);

                return result;
            }

            if (currentRuntimeContext.getCompiledGraph() == null) {
                result = agentRuntimeContextBuilder.enrich(currentRuntimeContext, agentGraph);

                agentSessionManage.updateAgentRuntimeContext(agentSession, currentRuntimeContext);

                return result;
            }

            if (currentRuntimeContext.getGraphRunStatus() >= 1) {
                result = currentRuntimeContext;
                return result;
            }

            return result;
        }

        @Override
        public void onStartGraph(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext) {
            agentRuntimeContext.setGraphRunStatus(1);
            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            if (agentRuntimeContext != null) {
                this.agentSessionManage.updateAgentRuntimeContext(agentSession, agentRuntimeContext);
            }
        }

        @Override
        public void onEndGraph(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext) {
            agentRuntimeContext.setGraphRunStatus(2);
            agentRuntimeContext.setIsEnd(true);
            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            if (agentRuntimeContext != null) {
                this.agentSessionManage.updateAgentRuntimeContext(agentSession, agentRuntimeContext);
            }
        }

        @Override
        public void onInvokeStream(IAgent agent, IAgentEvent agentEvent,
                IAgentRuntimeContext agentRuntimeContext, StreamContent content) {

            if (agentRuntimeContext.isEnd()) {
                return;
            }

            if (content.isError()) {
                onError(agent, agentEvent, agentRuntimeContext, new Exception(content.getContent()));
                return;
            } else if (!content.isComplete()) {
                ISseChanel sseChanel = agentEvent.getSseChanel();
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
        public void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) {
            if (agentRuntimeContext.isEnd()) {
                return;
            }

            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            IAgentRuntimeContext currentRuntimeContext = agentSession.getCurrentRuntimeContext();
            if (currentRuntimeContext.getAgentRuntimeContextId()
                    .equals(agentRuntimeContext.getAgentRuntimeContextId()) && !currentRuntimeContext.isEnd()) {
                currentRuntimeContext.setIsEnd(true);
                IAgentEvent currentEvent = currentRuntimeContext.getCurrentEvent();
                currentEvent.setAgentResponse(agentResponse);

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
            IAgentRuntimeContext currentRuntimeContext = agentSession.getCurrentRuntimeContext();
            if (currentRuntimeContext.getAgentRuntimeContextId()
                    .equals(agentRuntimeContext.getAgentRuntimeContextId()) && !currentRuntimeContext.isEnd()) {
                IAgentEvent currentEvent = currentRuntimeContext.getCurrentEvent();
                currentEvent.setAgentResponse(agentResponse);

                endSession(agent, agentEvent, agentResponse, agentSession);
            }
        }

        private void endSession(IAgent agent, IAgentEvent agentEvent, IAgentResponse agentResponse,
                IAgentSession agentSession) {
            String agentName = "";
            if (agent != null) {
                agentName = agent.getName();
            }
            this.agentSessionManage.addSessionItem(agentSession, AgentSessionItem.builder()
                    .agentName(agentName)
                    .agentRequest(agentEvent.getAgentRequest())
                    .agentResponse(null)
                    .build());

            this.agentSessionManage.addSessionItem(agentSession, AgentSessionItem.builder()
                    .agentName(agentName)
                    .agentRequest(null)
                    .agentResponse(agentEvent.getAgentResponse())
                    .build());

            IAgentRuntimeContext currentRuntimeContext = agentSession.getCurrentRuntimeContext();
            if (currentRuntimeContext != null) {

                this.agentSessionManage.updateAgentRuntimeContext(agentSession, currentRuntimeContext);
            }

            ISseChanel sseChanel = agentEvent.getSseChanel();
            if (sseChanel != null
                    && (agentSession.getParentSessionId() == null || agentSession.getParentSessionId().isEmpty())) {
                sseChanel.tryEmitComplete();
            }
        }

    }

    public interface IIntentComplete {
        void exec(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse);
    }

    public interface IIntentError {
        void exec(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                Throwable throwable);
    }

}
