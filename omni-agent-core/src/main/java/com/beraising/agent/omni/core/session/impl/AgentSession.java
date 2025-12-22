package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.impl.AgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
/**
 * AgentSession 类表示一个代理会话，用于管理代理的会话信息和运行时上下文。
 * 该类实现了 IAgentSession 接口，提供了会话管理的基本功能。
 */
@Data
public class AgentSession implements IAgentSession {

    /**
     * 代理会话的唯一标识符
     */
    private String agentSessionId;

    /**
     * 父会话的唯一标识符
     */
    private String parentSessionId;

    /**
     * 用户类型，默认为普通用户
     */
    private EUserType userType = EUserType.USER;

    /**
     * 用户的唯一标识符
     */
    private String userId;

    /**
     * 代理会话项列表，使用 JSON 反序列化时将内容作为 AgentSessionItem 类型处理
     */
    @JsonDeserialize(contentAs = AgentSessionItem.class)
    private List<IAgentSessionItem> agentSessionItems;

    /**
     * 代理运行时上下文列表，使用 JSON 反序列化时将内容作为 AgentRuntimeContext 类型处理
     */
    @JsonDeserialize(contentAs = AgentRuntimeContext.class)
    private List<IAgentRuntimeContext> agentRuntimeContexts;

    /**
     * AgentSession 的构造函数
     * 初始化 agentSessionItems 和 agentRuntimeContexts 为空的 ArrayList
     */
    public AgentSession() {
        super();
        this.agentSessionItems = new ArrayList<>();
        this.agentRuntimeContexts = new ArrayList<>();
    }

    /**
     * 获取当前运行时上下文
     *
     * @return 返回 agentRuntimeContexts 列表中的最后一个元素，如果列表为空则返回 null
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    @Override
    public IAgentRuntimeContext getCurrentRuntimeContext() {
        return ListUtils.lastOf(this.agentRuntimeContexts);
    }

}

