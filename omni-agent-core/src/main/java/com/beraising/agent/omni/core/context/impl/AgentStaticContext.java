package com.beraising.agent.omni.core.context.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverConstant;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
/**
 * AgentStaticContext类提供了代理静态上下文的实现，用于管理代理相关的各种组件和服务。
 * 该类通过依赖注入的方式初始化各个组件，并提供获取这些组件的方法。
 */
@Component
public class AgentStaticContext implements IAgentStaticContext {
    private final ChatClient.Builder chatClientBuilder;
    private final ChatMemoryRepository memoryRepository;
    private final AgentRegistry agentRegistry;
    private final IAgentEngine agentEngine;
    private SaverConfig graphSaverConfig;
    private final IAgentSessionManage agentSessionManage;


    /**
     * 构造函数，初始化AgentStaticContext实例
     *
     * @param agentEngine 代理引擎实例，用于处理代理的核心逻辑
     * @param agentRegistry 代理注册表，用于管理可用的代理
     * @param chatClientBuilder 聊天客户端构建器，用于创建聊天客户端实例
     * @param memoryRepository 聊天内存仓库，用于存储和检索聊天记忆
     * @param agentSessionManage 代理会话管理器，用于管理代理会话
     */
    public AgentStaticContext(IAgentEngine agentEngine, AgentRegistry agentRegistry,
            ChatClient.Builder chatClientBuilder,
            ChatMemoryRepository memoryRepository, IAgentSessionManage agentSessionManage) {
        this.agentRegistry = agentRegistry;
        this.chatClientBuilder = chatClientBuilder;
        this.memoryRepository = memoryRepository;
        this.agentEngine = agentEngine;
        // 初始化图保存配置，注册内存保存器
        this.graphSaverConfig = SaverConfig.builder().register(SaverConstant.MEMORY, new MemorySaver()).build();
        this.agentSessionManage = agentSessionManage;

        // 为聊天客户端构建器添加默认顾问
        this.chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor());
    }

    /**
     * 获取代理注册表
     *
     * @return AgentRegistry 代理注册表实例
     */
    @Override
    public AgentRegistry getAgentRegistry() {
        return agentRegistry;
    }

    /**
     * 获取聊天客户端构建器
     *
     * @return ChatClient.Builder 聊天客户端构建器实例
     */
    @Override
    public ChatClient.Builder getChatClientBuilder() {
        return chatClientBuilder;
    }

    /**
     * 获取聊天内存仓库
     *
     * @return ChatMemoryRepository 聊天内存仓库实例
     */
    @Override
    public ChatMemoryRepository getMemoryRepository() {
        return memoryRepository;
    }

    /**
     * 获取代理引擎
     *
     * @return IAgentEngine 代理引擎实例
     */
    @Override
    public IAgentEngine getAgentEngine() {
        return agentEngine;
    }

    /**
     * 获取图保存配置
     *
     * @return SaverConfig 图保存配置实例
     */
    @Override
    public SaverConfig getGraphSaverConfig() {
        return graphSaverConfig;
    }

    /**
     * 获取代理会话管理器
     *
     * @return IAgentSessionManage 代理会话管理器实例
     */
    @Override
    public IAgentSessionManage getAgentSessionManage() {
        return agentSessionManage;
    }

}
