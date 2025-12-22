package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
/**
 * 代理引擎接口，定义了代理会话管理和事件调用的核心功能
 */
public interface IAgentEngine {

    /**
     * 获取代理会话管理器实例
     *
     * @return IAgentSessionManage 代理会话管理器接口实例
     */
    IAgentSessionManage getAgentSessionManage();

    /**
     * 调用代理事件处理方法
     *
     * @param agentEvent 代理事件对象，包含需要处理的事件信息
     * @return IAgentEvent 处理后的代理事件结果
     * @throws Exception 当事件处理过程中发生异常时抛出
     */
    IAgentEvent invoke(IAgentEvent agentEvent) throws Exception;

}

