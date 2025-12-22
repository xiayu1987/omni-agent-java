package com.beraising.agent.omni.core.context;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;

import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

/**
 * IAgentStaticContext接口定义了代理静态上下文的功能契约。
 * 该接口继承自IAgentContext，提供了获取各种代理相关组件和服务的方法。
 */
public interface IAgentStaticContext extends IAgentContext {

    /**
     * 获取代理注册表实例。
     *
     * @return AgentRegistry 代理注册表对象，用于管理代理的注册和发现
     */
    AgentRegistry getAgentRegistry();

    /**
     * 获取聊天客户端构建器实例。
     *
     * @return ChatClient.Builder 聊天客户端构建器，用于创建聊天客户端实例
     */
    ChatClient.Builder getChatClientBuilder();

    /**
     * 获取聊天内存仓库实例。
     *
     * @return ChatMemoryRepository 聊天内存仓库对象，用于管理聊天会话的内存存储
     */
    ChatMemoryRepository getMemoryRepository();

    /**
     * 获取代理引擎实例。
     *
     * @return IAgentEngine 代理引擎对象，用于执行代理的核心逻辑
     */
    IAgentEngine getAgentEngine();

    /**
     * 获取图保存器配置实例。
     *
     * @return SaverConfig 图保存器配置对象，用于配置图形数据的保存行为
     */
    SaverConfig getGraphSaverConfig();

    /**
     * 获取代理会话管理器实例。
     *
     * @return IAgentSessionManage 代理会话管理器对象，用于管理代理会话的生命周期
     */
    IAgentSessionManage getAgentSessionManage();

}
