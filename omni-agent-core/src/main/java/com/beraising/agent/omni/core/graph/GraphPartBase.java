package com.beraising.agent.omni.core.graph;

import org.springframework.ai.chat.client.ChatClient;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.session.IAgentSession;
/**
 * 抽象基类，用于实现图节点或图组件的基础功能。
 * 提供了与图、代理会话及状态管理相关的基本方法。
 *
 * @param <T> 实现 {@link IGraphState} 接口的状态类型
 */
public abstract class GraphPartBase<T extends IGraphState> implements IGraphPart {

    private IAgentGraph graph;
    private String name;

    /**
     * 构造函数，初始化图组件名称和所属图实例。
     *
     * @param name  图组件的名称
     * @param graph 所属的图实例
     */
    public GraphPartBase(String name, IAgentGraph graph) {
        this.name = name;
        this.graph = graph;
    }

    /**
     * 获取图组件的名称。
     *
     * @return 图组件名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取当前图组件所关联的图实例。
     *
     * @return 关联的图实例
     */
    @Override
    public IAgentGraph getGraph() {
        return graph;
    }

    /**
     * 获取聊天客户端构建器，用于创建聊天客户端实例。
     *
     * @return 聊天客户端构建器
     */
    protected ChatClient.Builder getChatClientBuilder() {
        return graph.getAgent().getAgentStaticContext().getChatClientBuilder();
    }

    /**
     * 构建并返回一个新的聊天客户端实例。
     *
     * @return 聊天客户端实例
     */
    protected ChatClient getChatClient() {
        return graph.getAgent().getAgentStaticContext().getChatClientBuilder().build();
    }

    /**
     * 获取代理静态上下文信息。
     *
     * @return 代理静态上下文对象
     */
    protected IAgentStaticContext getAgentStaticContext() {
        return graph.getAgent().getAgentStaticContext();
    }

    /**
     * 根据全局状态获取对应的图状态及相关运行时上下文信息。
     * 包括校验 Session ID 是否存在、查找对应的 Agent Session 和 Runtime Context，
     * 并将全局状态设置到图状态中。
     *
     * @param state 全局状态对象
     * @return 包含图状态、运行时上下文和当前事件的状态信息对象
     * @throws Exception 当无法找到有效的 Session 或相关上下文时抛出异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public StateInfo getGraphState(OverAllState state) throws Exception {
        // 获取 Session ID 并进行空值检查
        String sessionId = state.value(IGraphState.getAgentSessionIDKey(), "");

        if (sessionId == null) {
            throw new Exception("Session ID cannot be null");
        }

        // 查找对应的 Agent Session
        IAgentSession agentSession = graph.getAgent().getAgentStaticContext().getAgentSessionManage()
                .getAgentSessionById(sessionId);

        if (agentSession == null) {
            throw new Exception("Agent session not found for session ID: " + sessionId);
        }

        // 获取当前运行时上下文
        IAgentRuntimeContext currentRuntimeContext = agentSession.getCurrentRuntimeContext();

        if (currentRuntimeContext == null) {
            throw new Exception("Agent runtime context not found for session ID: " + sessionId);
        }

        // 获取图状态并更新其内部状态
        IGraphState graphState = currentRuntimeContext.getGraphState();

        if (graphState == null) {
            throw new Exception("Graph state not found for session ID: " + sessionId);
        }

        graphState.setState(state);

        // 获取当前处理的事件
        IAgentEvent currentEvent = currentRuntimeContext.getCurrentEvent();

        // 返回封装好的状态信息对象
        return new StateInfo((T) graphState, currentRuntimeContext, currentEvent);
    }
}

