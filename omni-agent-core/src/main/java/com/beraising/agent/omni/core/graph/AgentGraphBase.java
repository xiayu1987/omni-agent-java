package com.beraising.agent.omni.core.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.event.IEventListener.StreamContent;
import com.beraising.agent.omni.core.exception.BusinessException;
import com.beraising.agent.omni.core.graph.edge.IGraphEdge;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphStateValue;

/**
 * 抽象基类，用于实现图结构代理（Agent Graph）的基本功能。
 * 提供了节点、边、事件监听器等基础组件的管理，并定义了图执行流程的核心逻辑。
 *
 * @param <T> 实现 {@link IGraphState} 接口的状态类型
 */
public abstract class AgentGraphBase<T extends IGraphState> implements IAgentGraph {

    private IAgent agent;
    private IAgentGraphListener agentGraphListener;
    private List<IGraphNode> graphNodes;
    private List<IGraphEdge> graphEdges;
    private IEventListener eventListener;

    /**
     * 构造方法，初始化节点列表和边列表为空集合。
     */
    public AgentGraphBase() {
        super();
        graphNodes = new ArrayList<>();
        graphEdges = new ArrayList<>();
    }

    /**
     * 设置当前代理对象。
     *
     * @param agent 当前代理对象
     */
    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    /**
     * 获取当前代理对象。
     *
     * @return 当前代理对象
     */
    @Override
    public IAgent getAgent() {
        return agent;
    }

    /**
     * 设置代理图监听器。
     *
     * @param agentGraphListener 图监听器实例
     */
    @Override
    public void setAgentGraphListener(IAgentGraphListener agentGraphListener) {
        this.agentGraphListener = agentGraphListener;
    }

    /**
     * 获取代理图监听器。
     *
     * @return 图监听器实例
     */
    @Override
    public IAgentGraphListener getAgentGraphListener() {
        return agentGraphListener;
    }

    /**
     * 设置事件监听器。
     *
     * @param eventListener 事件监听器实例
     */
    @Override
    public void setEventListener(IEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * 获取事件监听器。
     *
     * @return 事件监听器实例
     */
    @Override
    public IEventListener getEventListener() {
        return eventListener;
    }

    /**
     * 设置图中的所有节点。
     *
     * @param graphNodes 节点列表
     */
    @Override
    public void setGraphNodes(List<IGraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }

    /**
     * 获取图中所有的节点。
     *
     * @return 节点列表
     */
    @Override
    public List<IGraphNode> getGraphNodes() {
        return graphNodes;
    }

    /**
     * 设置图中的所有边。
     *
     * @param graphEdges 边列表
     */
    public void setGraphEdges(List<IGraphEdge> graphEdges) {
        this.graphEdges = graphEdges;
    }

    /**
     * 获取图中所有的边。
     *
     * @return 边列表
     */
    @Override
    public List<IGraphEdge> getGraphEdges() {
        return graphEdges;
    }

    /**
     * 初始化代理图运行环境。
     *
     * @param agent               代理对象
     * @param eventListener       事件监听器
     * @param agentGraphListener  图监听器
     * @throws Exception 初始化过程中可能抛出异常
     */
    @Override
    public void init(IAgent agent, IEventListener eventListener, IAgentGraphListener agentGraphListener)
            throws Exception {
        this.agent = agent;
        this.agentGraphListener = agentGraphListener;
        this.eventListener = eventListener;
    }

    /**
     * 执行代理图逻辑。根据运行上下文状态决定是首次启动还是恢复执行。
     *
     * @param agentRuntimeContext 运行时上下文信息
     * @return 当前处理的事件对象
     * @throws Exception 执行过程可能出现的异常
     */
    @Override
    public IAgentEvent invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception {

        // 检查是否有待处理的事件
        if (agentRuntimeContext.getAgentEvents().size() == 0) {
            throw new Exception("AgentRuntimeContext has no AgentEvent");
        }

        // 配置线程ID
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .threadId(agentRuntimeContext.getAgentRuntimeContextId())
                .build();

        IAgentEvent currentEvent = agentRuntimeContext.getCurrentEvent();

        // 判断是否为首次执行
        if (agentRuntimeContext.getGraphRunStatus() == 0) {

            eventListener.onStartGraph(agent, currentEvent, agentRuntimeContext);

            // 处理流式输出场景
            if (currentEvent != null && currentEvent.isStream()) {
                AsyncGenerator<NodeOutput> asyncGenerator = agentRuntimeContext.getCompiledGraph()
                        .stream(createInput(currentEvent, agentRuntimeContext), runnableConfig);

                invokeGraphStream(agentRuntimeContext, currentEvent, asyncGenerator);
            } else {
                // 同步调用图执行
                agentRuntimeContext.getCompiledGraph()
                        .invoke(createInput(currentEvent, agentRuntimeContext), runnableConfig);
            }

        } else if (agentRuntimeContext.getGraphRunStatus() >= 1) {

            // 恢复执行：获取之前保存的状态并继续执行
            StateSnapshot stateSnapshot = agentRuntimeContext.getCompiledGraph().getState(runnableConfig);
            OverAllState state = stateSnapshot.state();
            agentRuntimeContext.getGraphState().setState(state);
            state.withResume();

            // 查找下一个要执行的节点
            IGraphNode nextNode = this.getGraphNodes().stream()
                    .filter(node -> node.getName().equals(stateSnapshot.config().nextNode().orElse("")))
                    .findFirst().orElse(null);

            if (nextNode == null) {
                throw new Exception("Next node not found");
            }

            // 注入用户反馈数据
            state.withHumanFeedback(
                    new OverAllState.HumanFeedback(createFeedBack(currentEvent, agentRuntimeContext, nextNode), ""));

            // 根据是否为流模式选择不同的执行方式
            if (currentEvent != null && currentEvent.isStream()) {
                AsyncGenerator<NodeOutput> asyncGenerator = agentRuntimeContext.getCompiledGraph()
                        .streamFromInitialNode(state, runnableConfig);

                invokeGraphStream(agentRuntimeContext, currentEvent, asyncGenerator);
            } else {
                agentRuntimeContext.getCompiledGraph()
                        .invoke(state, runnableConfig);
            }

        }

        return currentEvent;

    }

    /**
     * 异步处理图执行结果流。
     *
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param asyncGenerator      输出流生成器
     */
    private void invokeGraphStream(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            AsyncGenerator<NodeOutput> asyncGenerator) {
        Executors.newSingleThreadExecutor().submit(() -> {
            asyncGenerator.forEachAsync(output -> {
                try {
                    if (output instanceof StreamingOutput streamingOutput) {

                        eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                                StreamContent.builder().isComplete(false).isError(false)
                                        .content(streamingOutput.chunk()).build());

                    }

                } catch (Exception e) {
                    eventListener.onError(agent, agentEvent, agentRuntimeContext, e);
                }

            }).thenAccept(args -> {

                eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                        StreamContent.builder().isComplete(true).isError(false)
                                .build());

            }).exceptionally(exception -> {
                BusinessException be = unwrap(exception, BusinessException.class);
                if (be != null) {
                    eventListener.onError(agent, agentEvent, agentRuntimeContext, be);
                } else {
                    eventListener.onError(agent, agentEvent, agentRuntimeContext, exception);
                }

                return null;
            });
        });
    }

    /**
     * 在图部分应用完成时触发回调。
     *
     * @param graphPart           应用的部分图结构
     * @param updatedGraphState   更新后的图状态
     * @param graphState          原始图状态
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param <T>                 泛型参数，表示图状态的具体类型
     */
    @SuppressWarnings({ "unchecked", "hiding" })
    @Override
    public <T extends IGraphState> void onGraphPartApplield(IGraphPart graphPart,
            IUpdatedGraphState<T> updatedGraphState,
            IGraphState graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) {
        if (agentEvent.isStream() && updatedGraphState instanceof IUpdatedGraphStateValue updatedGraphStateValue) {

            Map<String, Object> stateMap = updatedGraphStateValue.exec();
            eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                    StreamContent.builder().isComplete(false).isError(false)
                            .content(stateMap.values().stream().map(Object::toString).collect(Collectors.joining("\n")))
                            .build());

        }
    }

    /**
     * 将输入数据注入到图状态中。
     *
     * @param input               输入数据映射表
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     */
    @SuppressWarnings("unchecked")
    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) {
        putInput(input, agentRuntimeContext, agentEvent, (T) agentRuntimeContext.getGraphState());
    }

    /**
     * 将反馈数据注入到指定节点的状态中。
     *
     * @param feedBack            反馈数据映射表
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param graphNode           目标节点
     */
    @SuppressWarnings("unchecked")
    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode) {
        putFeedBack(feedBack, agentRuntimeContext, agentEvent, graphNode, (T) agentRuntimeContext.getGraphState());
    }

    /**
     * 创建代理响应对象。
     *
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param graphNode           对应的图节点
     * @return 生成的代理响应对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode) {

        IAgentResponse agentResponse = createOutput(agentRuntimeContext, agentEvent, graphNode,
                (T) agentRuntimeContext.getGraphState());

        return agentResponse;
    }

    /**
     * 子类需实现该方法以将输入数据注入到特定类型的图状态中。
     *
     * @param input               输入数据映射表
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param graphState          图状态对象
     */
    public abstract void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, T graphState);

    /**
     * 子类需实现该方法以将反馈数据注入到特定类型的图状态中。
     *
     * @param feedBack            反馈数据映射表
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param graphNode           目标节点
     * @param graphState          图状态对象
     */
    public abstract void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, T graphState);

    /**
     * 子类需实现该方法以创建代理响应对象。
     *
     * @param agentRuntimeContext 运行时上下文
     * @param agentEvent          当前事件
     * @param graphNode           对应的图节点
     * @param graphState          图状态对象
     * @return 生成的代理响应对象
     */
    public abstract IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, T graphState);

    /**
     * 解包嵌套异常链，查找指定类型的异常。
     *
     * @param e    异常对象
     * @param type 要查找的异常类型
     * @param <T>  泛型参数，表示异常类型
     * @return 匹配的第一个异常或null
     */
    public static <T extends Throwable> T unwrap(Throwable e, Class<T> type) {
        while (e != null) {
            if (type.isInstance(e)) {
                return type.cast(e);
            }
            e = e.getCause();
        }
        return null;
    }
}
