package com.beraising.agent.omni.core.context.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.fastjson.annotation.JSONField;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
/**
 * AgentRuntimeContext 类表示代理运行时上下文，用于管理代理执行过程中的状态、事件及相关信息。
 * 实现了 IAgentRuntimeContext 接口，提供对图状态、代理实例、编译后的图结构等的访问与控制。
 */
public class AgentRuntimeContext implements IAgentRuntimeContext {

    /**
     * 存储代理事件列表。使用 @JsonDeserialize 指定反序列化时将内容作为 AgentEvent 处理。
     */
    @JsonDeserialize(contentAs = AgentEvent.class)
    private List<IAgentEvent> agentEvents;

    /**
     * 图状态对象，标记为 transient 和 @JsonIgnore 表示在序列化时不参与 JSON 转换。
     */
    @JsonIgnore
    private transient IGraphState graphState;

    /**
     * 当前代理实例，标记为 transient 和 @JsonIgnore 表示在序列化时不参与 JSON 转换。
     */
    @JsonIgnore
    private transient IAgent agent;

    /**
     * 编译后的图结构，标记为 transient 和 @JsonIgnore 表示在序列化时不参与 JSON 转换。
     */
    @JsonIgnore
    private transient CompiledGraph compiledGraph;

    /**
     * 代理名称。
     */
    private String agentName;

    /**
     * 代理会话 ID。
     */
    private String agentSessionId;

    /**
     * 代理运行时上下文 ID。
     */
    private String agentRuntimeContextId;

    /**
     * 标记是否结束执行流程。
     */
    @JsonProperty("end")
    private boolean isEnd;

    /**
     * 图运行状态码。
     */
    private int graphRunStatus;

    /**
     * 构造函数初始化默认值：
     * - 初始化 agentEvents 列表为空列表；
     * - 设置 isEnd 默认为 false。
     */
    public AgentRuntimeContext() {
        super();
        this.agentEvents = new ArrayList<>();
        this.isEnd = false;
    }

    /**
     * 获取当前图状态。
     *
     * @return 返回当前图状态对象。
     */
    public IGraphState getGraphState() {
        return graphState;
    }

    /**
     * 设置当前图状态。
     *
     * @param graphState 图状态对象。
     */
    public void setGraphState(IGraphState graphState) {
        this.graphState = graphState;
    }

    /**
     * 获取所有代理事件。
     *
     * @return 返回代理事件列表。
     */
    @Override
    public List<IAgentEvent> getAgentEvents() {
        return agentEvents;
    }

    /**
     * 设置代理事件列表。
     *
     * @param agentEvents 新的代理事件列表。
     */
    @Override
    public void setAgentEvents(List<IAgentEvent> agentEvents) {
        this.agentEvents = agentEvents;
    }

    /**
     * 获取当前代理实例。
     *
     * @return 返回当前代理实例。
     */
    @Override
    public IAgent getAgent() {
        return agent;
    }

    /**
     * 设置当前代理实例。
     *
     * @param agent 新的代理实例。
     */
    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    /**
     * 获取已编译的图结构。
     *
     * @return 返回编译后的图结构。
     */
    @Override
    public CompiledGraph getCompiledGraph() {
        return compiledGraph;
    }

    /**
     * 设置编译后的图结构。
     *
     * @param compiledGraph 已编译的图结构。
     */
    @Override
    public void setCompiledGraph(CompiledGraph compiledGraph) {
        this.compiledGraph = compiledGraph;
    }

    /**
     * 查询是否已经到达执行终点。
     *
     * @return 如果是则返回 true，否则返回 false。
     */
    @Override
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * 设置执行终点标志位。
     *
     * @param isEnd 是否结束执行。
     */
    @Override
    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    /**
     * 获取代理名称。
     *
     * @return 返回代理名称字符串。
     */
    @Override
    public String getAgentName() {
        return agentName;
    }

    /**
     * 设置代理名称。
     *
     * @param agentName 代理名称。
     */
    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * 获取代理会话 ID。
     *
     * @return 返回代理会话 ID 字符串。
     */
    @Override
    public String getAgentSessionId() {
        return agentSessionId;
    }

    /**
     * 设置代理会话 ID。
     *
     * @param agentSessionId 代理会话 ID。
     */
    @Override
    public void setAgentSessionId(String agentSessionId) {
        this.agentSessionId = agentSessionId;
    }

    /**
     * 获取代理运行时上下文 ID。
     *
     * @return 返回代理运行时上下文 ID 字符串。
     */
    @Override
    public String getAgentRuntimeContextId() {
        return agentRuntimeContextId;
    }

    /**
     * 设置代理运行时上下文 ID。
     *
     * @param agentRuntimeContextId 运行时上下文 ID。
     */
    @Override
    public void setAgentRuntimeContextId(String agentRuntimeContextId) {
        this.agentRuntimeContextId = agentRuntimeContextId;
    }

    /**
     * 获取图运行状态码。
     *
     * @return 返回图运行状态整型值。
     */
    @Override
    public int getGraphRunStatus() {
        return graphRunStatus;
    }

    /**
     * 设置图运行状态码。
     *
     * @param graphRunStatus 图运行状态码。
     */
    @Override
    public void setGraphRunStatus(int graphRunStatus) {
        this.graphRunStatus = graphRunStatus;
    }

    /**
     * 根据用户类型筛选并获取对应的代理事件列表。
     *
     * @param userType 用户类型枚举。
     * @return 匹配指定用户类型的代理事件列表。
     */
    @Override
    public List<IAgentEvent> getAgentEventsByUserType(EUserType userType) {
        return agentEvents.stream().filter(agentEvent -> agentEvent.getUserType().equals(userType)).toList();
    }

    /**
     * 获取最新的一个代理事件（即最后一个）。
     *
     * @return 最后一个代理事件；如果列表为空，则可能抛出异常或返回 null（取决于 ListUtils.lastOf 的实现）。
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    @Override
    public IAgentEvent getCurrentEvent() {
        return ListUtils.lastOf(agentEvents);
    }

}

