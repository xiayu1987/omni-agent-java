package com.beraising.agent.omni.core.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.edge.IGraphEdge;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
/**
 * IAgentGraph 接口定义了代理图（Agent Graph）的核心行为。
 * 它用于初始化、执行以及管理一个由节点和边组成的图结构，
 * 并提供与代理运行时上下文交互的方法。
 */
public interface IAgentGraph {

    /**
     * 初始化代理图。
     *
     * @param agent               代理实例
     * @param eventListener       事件监听器
     * @param agentGraphListener  代理图状态变化监听器
     * @throws Exception 初始化过程中可能抛出的异常
     */
    void init(IAgent agent, IEventListener eventListener,
              IAgentGraphListener agentGraphListener) throws Exception;

    /**
     * 执行代理图逻辑。
     *
     * @param agentRuntimeContext 代理运行时上下文
     * @return 返回处理后的代理事件对象
     * @throws Exception 执行中可能出现的异常
     */
    IAgentEvent invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception;

    /**
     * 获取当前的状态图表示。
     *
     * @param keyStrategyFactory 键策略工厂，用于构建键值映射
     * @return 当前状态图对象
     * @throws Exception 获取状态图过程中的异常
     */
    StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception;

    /**
     * 设置代理实例。
     *
     * @param agent 要设置的代理对象
     */
    void setAgent(IAgent agent);

    /**
     * 获取当前代理实例。
     *
     * @return 当前代理对象
     */
    IAgent getAgent();

    /**
     * 设置代理图监听器。
     *
     * @param agentGraphListener 图监听器实例
     */
    void setAgentGraphListener(IAgentGraphListener agentGraphListener);

    /**
     * 获取当前代理图监听器。
     *
     * @return 当前图监听器对象
     */
    IAgentGraphListener getAgentGraphListener();

    /**
     * 设置事件监听器。
     *
     * @param eventListener 事件监听器实例
     */
    void setEventListener(IEventListener eventListener);

    /**
     * 获取当前事件监听器。
     *
     * @return 当前事件监听器对象
     */
    IEventListener getEventListener();

    /**
     * 设置图的所有节点列表。
     *
     * @param graphNodes 节点列表
     */
    void setGraphNodes(List<IGraphNode> graphNodes);

    /**
     * 获取图的所有节点列表。
     *
     * @return 当前节点列表
     */
    List<IGraphNode> getGraphNodes();

    /**
     * 设置图的所有边列表。
     *
     * @param graphEdges 边列表
     */
    void setGraphEdges(List<IGraphEdge> graphEdges);

    /**
     * 获取图的所有边列表。
     *
     * @return 当前边列表
     */
    List<IGraphEdge> getGraphEdges();

    /**
     * 创建一个新的图状态对象。
     *
     * @return 新创建的图状态对象
     */
    IGraphState newGraphState();

    /**
     * 当某个图部分被应用后触发此方法。
     *
     * @param graphPart           已应用的图部分
     * @param updatedGraphState   更新后的图状态包装器
     * @param graphState          原始图状态
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          触发该操作的代理事件
     * @param <T>                 泛型类型，继承自 IGraphState
     */
    <T extends IGraphState> void onGraphPartApplield(IGraphPart graphPart, IUpdatedGraphState<T> updatedGraphState,
                                                    IGraphState graphState, IAgentRuntimeContext agentRuntimeContext,
                                                    IAgentEvent agentEvent);

    /**
     * 默认方法：根据代理事件和运行时上下文创建输入数据映射。
     * 包含会话 ID 和运行时上下文 ID，并调用 putInput 方法填充其他字段。
     *
     * @param agentEvent          代理事件
     * @param agentRuntimeContext 运行时上下文
     * @return 输入数据映射
     */
    default Map<String, Object> createInput(IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext) {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(IGraphState.getAgentSessionIDKey(), agentRuntimeContext.getAgentSessionId());
        inputMap.put(IGraphState.getAgentRuntimeContextIDKey(), agentRuntimeContext.getAgentRuntimeContextId());
        putInput(inputMap, agentRuntimeContext, agentEvent);
        return inputMap;
    }

    /**
     * 默认方法：基于代理事件、运行时上下文及图节点创建反馈信息映射。
     * 内部通过 putFeedBack 方法填充具体内容。
     *
     * @param agentEvent          代理事件
     * @param agentRuntimeContext 运行时上下文
     * @param graphNode           相关图节点
     * @return 反馈信息映射
     */
    default Map<String, Object> createFeedBack(IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                                               IGraphNode graphNode) {
        Map<String, Object> inputMap = new HashMap<>();
        putFeedBack(inputMap, agentRuntimeContext, agentEvent, graphNode);

        return inputMap;
    }

    /**
     * 根据运行时上下文、代理事件和相关图节点创建输出响应对象。
     *
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          代理事件
     * @param graphNode           操作涉及的图节点
     * @return 输出响应对象
     */
    IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
                                IGraphNode graphNode);

    /**
     * 将额外的数据放入输入映射中。
     *
     * @param input               输入映射
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          代理事件
     */
    void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
                  IAgentEvent agentEvent);

    /**
     * 将反馈数据放入反馈映射中。
     *
     * @param feedBack            反馈映射
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          代理事件
     * @param graphNode           图节点
     */
    void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
                     IAgentEvent agentEvent, IGraphNode graphNode);

}
