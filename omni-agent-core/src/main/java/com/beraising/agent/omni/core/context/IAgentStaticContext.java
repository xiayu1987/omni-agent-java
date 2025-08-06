package com.beraising.agent.omni.core.context;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;

import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.session.IAgentSessionManage;

public interface IAgentStaticContext extends IAgentContext {

    AgentRegistry getAgentRegistry();

    ChatClient.Builder getChatClientBuilder();

    ChatMemoryRepository getMemoryRepository();

    IAgentEngine getAgentEngine();

    SaverConfig getGraphSaverConfig();

    IAgentSessionManage getAgentSessionManage();

}
