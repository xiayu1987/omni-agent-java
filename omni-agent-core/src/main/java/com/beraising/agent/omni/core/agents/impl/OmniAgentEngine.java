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
/**
 * 全局智能体引擎实现类，负责协调多个智能体之间的调用与交互。
 * <p>
 * 该类实现了 {@link IAgentEngine} 接口，并通过注册中心获取不同类型的智能体（如意图识别智能体），
 * 并管理其生命周期、会话状态以及事件流转。同时支持流式响应和错误处理机制。
 * </p>
 */
@Component
public class OmniAgentEngine implements IAgentEngine {

    private final AgentRegistry agentRegistry;
    private final IAgentSessionManage agentSessionManage;

    private final IAgentRuntimeContextBuilder agentRuntimeContextBuilder;
    private IEventListener eventListener;
    private IntentEventListener intentEventListener;

    /**
     * 构造方法初始化各个依赖组件。
     *
     * @param agentRegistry              智能体注册中心，用于查找并加载各类智能体实例
     * @param agentSessionManage         会话管理器，用于创建和维护用户/系统的会话信息
     * @param agentRuntimeContextBuilder 上下文构建器，用于初始化或更新运行时上下文环境
     */
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

    /**
     * 获取当前使用的会话管理器。
     *
     * @return 返回已注入的会话管理器对象
     */
    public IAgentSessionManage getAgentSessionManage() {
        return this.agentSessionManage;
    }

    /**
     * 启动一次智能体调用流程。
     * <p>
     * 首先检查是否存在已有会话；若无则新建一个会话。然后构造意图识别所需的事件数据，
     * 初始化意图识别监听器，并触发意图识别智能体执行逻辑。
     * </p>
     *
     * @param agentEvent 用户发起的原始事件对象
     * @return 返回传入的原始事件对象（可能已被修改）
     * @throws Exception 若在调用过程中发生异常将抛出
     */
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

    /**
     * 处理意图识别过程中的错误回调。
     * <p>
     * 在意图识别失败后，将错误信息传递给主监听器进行统一处理，并通知前端 SSE 渠道。
     * </p>
     *
     * @param intentAgent               发生错误的意图识别智能体
     * @param intentEvent               引发错误的意图事件
     * @param intentAgentRuntimeContext 当前意图识别的运行上下文
     * @param throwable                 抛出的具体异常对象
     */
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

    /**
     * 处理意图识别完成后的后续动作。
     * <p>
     * 解析意图识别的结果 JSON 数据，判断是否成功、是否模糊、是否需要继续等条件，
     * 决定是提示用户确认、复用旧任务还是启动新任务。
     * </p>
     *
     * @param intentAgent               完成操作的意图识别智能体
     * @param intentEvent               被处理的意图事件
     * @param intentAgentRuntimeContext 当前意图识别的运行上下文
     * @param intentAgentResponse       意图识别返回的响应结果
     */
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
            String msg = obj.has("message") ? obj.get("message").getAsString() : "意图识别失败";

            if (!isSuccess) {
                eventListener.onError(intentAgent, userCurrentEvent, null, new Exception(msg));
                return;
            }

            boolean isContinue = obj.has("isContinue") && obj.get("isContinue").getAsBoolean();
            boolean isAmbiguous = obj.has("isAmbiguous") && obj.get("isAmbiguous").getAsBoolean();
            String nextIntentAgentName = obj.has("nextAgentName") ? obj.get("nextAgentName").getAsString() : "";

            // 情况 1: 模糊意图
            if (isAmbiguous) {
                // 这里不要直接切换或结束，而是提示用户确认
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
                            new Exception(msg));
                    return;
                } else {
                    eventListener.onEndGraph(userCurrentRuntimeContext.getAgent(), userCurrentEvent,
                            userCurrentRuntimeContext);
                }
            }

            if (nextAgent == null) {
                eventListener.onError(intentAgent, userCurrentEvent, userCurrentRuntimeContext,
                        new Exception(msg));
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

    /**
     * 创建用于意图识别的事件对象。
     * <p>
     * 包括拼接历史对话记录和当前请求内容作为 prompt 输入，供 LLM 判断下一步意图。
     * </p>
     *
     * @param userEvent 原始用户的事件对象
     * @return 构造好的意图识别事件对象
     */
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
     * 格式化单个事件日志，提取请求和响应内容组成字符串。
     *
     * @param event 待格式化的事件对象
     * @return 格式化后的字符串表示
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

    /**
     * 自定义意图识别事件监听器，继承自通用事件监听器。
     * <p>
     * 提供了对意图识别完成和错误两种情况的回调接口封装。
     * </p>
     */
    public class IntentEventListener extends EventListener {

        private IIntentComplete intentComplete;
        private IIntentError intentError;

        /**
         * 构造方法设置回调处理器。
         *
         * @param agentSessionManage         会话管理器
         * @param agentRuntimeContextBuilder 上下文构建器
         * @param intentComplete             成功回调接口
         * @param intentError                错误回调接口
         */
        public IntentEventListener(IAgentSessionManage agentSessionManage,
                IAgentRuntimeContextBuilder agentRuntimeContextBuilder, IIntentComplete intentComplete,
                IIntentError intentError) {
            super(agentSessionManage, agentRuntimeContextBuilder);
            this.intentComplete = intentComplete;
            this.intentError = intentError;
        }

        /**
         * 当意图识别完成后调用此方法。
         *
         * @param agent               执行完成的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param agentResponse       回应结果
         */
        @Override
        public void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse) {
            super.onComplete(agent, agentEvent, agentRuntimeContext, agentResponse);

            intentComplete.exec(agent, agentEvent, agentRuntimeContext, agentResponse);
        }

        /**
         * 当意图识别出现错误时调用此方法。
         *
         * @param agent               出错的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param throwable           异常对象
         */
        @Override
        public void onError(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                Throwable throwable) {

            super.onError(agent, agentEvent, agentRuntimeContext, throwable);

            intentError.exec(agent, agentEvent, agentRuntimeContext, throwable);
        }

    }

    /**
     * 通用事件监听器实现类，提供基础的事件处理能力。
     * <p>
     * 实现了 {@link IEventListener} 接口的所有方法，包括开始、协作、错误、完成等事件处理逻辑。
     * </p>
     */
    public class EventListener implements IEventListener {
        private IAgentSessionManage agentSessionManage;
        private IAgentRuntimeContextBuilder agentRuntimeContextBuilder;

        /**
         * 构造方法初始化相关依赖。
         *
         * @param agentSessionManage         会话管理器
         * @param agentRuntimeContextBuilder 上下文构建器
         */
        public EventListener(IAgentSessionManage agentSessionManage,
                IAgentRuntimeContextBuilder agentRuntimeContextBuilder) {
            super();
            this.agentSessionManage = agentSessionManage;
            this.agentRuntimeContextBuilder = agentRuntimeContextBuilder;
        }

        /**
         * 错误事件处理方法。
         *
         * @param agent               出错的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param throwable           异常对象
         */
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

        /**
         * 开始事件处理方法。
         *
         * @param parentSession 父级会话（可为空）
         * @param agentEvent    触发事件
         * @return 返回对应的会话对象
         * @throws Exception 若初始化失败则抛出异常
         */
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

        /**
         * 协作事件处理方法。
         *
         * @param agent      参与协作的智能体
         * @param userEvent  用户事件
         * @param collabEvent 协作事件
         */
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

        /**
         * 智能体调用前准备上下文的方法。
         *
         * @param agent       调用的智能体
         * @param agentEvent  触发事件
         * @param agentGraph  图结构描述
         * @return 返回准备好的运行上下文
         * @throws Exception 若准备失败则抛出异常
         */
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

        /**
         * 图形开始执行事件处理方法。
         *
         * @param agent               执行的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         */
        @Override
        public void onStartGraph(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext) {
            agentRuntimeContext.setGraphRunStatus(1);
            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            if (agentRuntimeContext != null) {
                this.agentSessionManage.updateAgentRuntimeContext(agentSession, agentRuntimeContext);
            }
        }

        /**
         * 图形执行结束事件处理方法。
         *
         * @param agent               执行的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         */
        @Override
        public void onEndGraph(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext) {
            agentRuntimeContext.setGraphRunStatus(2);
            agentRuntimeContext.setIsEnd(true);
            IAgentSession agentSession = agentSessionManage.getAgentSessionById(agentEvent.getAgentSessionId());
            if (agentRuntimeContext != null) {
                this.agentSessionManage.updateAgentRuntimeContext(agentSession, agentRuntimeContext);
            }
        }

        /**
         * 流式响应事件处理方法。
         *
         * @param agent               执行的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param content             流式内容片段
         */
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

        /**
         * 完成事件处理方法。
         *
         * @param agent               执行的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param agentResponse       最终响应结果
         */
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

        /**
         * 中断事件处理方法。
         *
         * @param agent               执行的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param agentResponse       中断原因响应
         */
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

        /**
         * 结束会话的操作方法。
         *
         * @param agent         执行的智能体
         * @param agentEvent    触发事件
         * @param agentResponse 响应结果
         * @param agentSession  对应的会话对象
         */
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

    /**
     * 意图识别完成回调接口。
     */
    public interface IIntentComplete {
        /**
         * 执行意图识别完成后的业务逻辑。
         *
         * @param agent               执行的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param agentResponse       响应结果
         */
        void exec(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IAgentResponse agentResponse);
    }

    /**
     * 意图识别错误回调接口。
     */
    public interface IIntentError {
        /**
         * 执行意图识别错误后的业务逻辑。
         *
         * @param agent               出错的智能体
         * @param agentEvent          触发事件
         * @param agentRuntimeContext 运行上下文
         * @param throwable           异常对象
         */
        void exec(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                Throwable throwable);
    }

}

