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
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.event.IEventListener.AgentGraphInvokeStreamContent;
import com.beraising.agent.omni.core.graph.edge.IGraphEdge;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphStateValue;

public abstract class AgentGraphBase<T extends IGraphState> implements IAgentGraph {

    private IAgent agent;
    private IAgentGraphListener agentGraphListener;
    private List<IGraphNode> graphNodes;
    private List<IGraphEdge> graphEdges;
    private IEventListener eventListener;

    public AgentGraphBase() {
        super();
        graphNodes = new ArrayList<>();
        graphEdges = new ArrayList<>();
    }

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    @Override
    public void setAgentGraphListener(IAgentGraphListener agentGraphListener) {
        this.agentGraphListener = agentGraphListener;
    }

    @Override
    public IAgentGraphListener getAgentGraphListener() {
        return agentGraphListener;
    }

    @Override
    public void setEventListener(IEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public IEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void setGraphNodes(List<IGraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }

    @Override
    public List<IGraphNode> getGraphNodes() {
        return graphNodes;
    }

    public void setGraphEdges(List<IGraphEdge> graphEdges) {
        this.graphEdges = graphEdges;
    }

    @Override
    public List<IGraphEdge> getGraphEdges() {
        return graphEdges;
    }

    @Override
    public void init(IAgent agent, IEventListener eventListener, IAgentGraphListener agentGraphListener)
            throws Exception {
        this.agent = agent;
        this.agentGraphListener = agentGraphListener;
        this.eventListener = eventListener;

    }

    @Override
    public IAgentEvent invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception {

        if (agentRuntimeContext.getAgentEvents().size() == 0) {
            throw new Exception("AgentRuntimeContext has no AgentEvent");
        }

        RunnableConfig runnableConfig = RunnableConfig.builder()
                .threadId(agentRuntimeContext.getAgentRuntimeContextId())
                .build();

        IAgentEvent agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());

        if (agentRuntimeContext.getAgentEvents().size() == 1) {

            if (agentEvent != null && agentEvent.isStream()) {
                AsyncGenerator<NodeOutput> asyncGenerator = agentRuntimeContext.getCompiledGraph()
                        .stream(createInput(agentEvent, agentRuntimeContext), runnableConfig);

                invokeGraphStream(agentRuntimeContext, agentEvent, asyncGenerator);
            } else {
                agentRuntimeContext.getCompiledGraph()
                        .invoke(createInput(agentEvent, agentRuntimeContext), runnableConfig);
            }

        }

        if (agentRuntimeContext.getAgentEvents().size() > 1) {

            StateSnapshot stateSnapshot = agentRuntimeContext.getCompiledGraph().getState(runnableConfig);
            OverAllState state = stateSnapshot.state();
            agentRuntimeContext.getGraphState().setState(state);
            state.withResume();

            IGraphNode nextNode = this.getGraphNodes().stream()
                    .filter(node -> node.getName().equals(stateSnapshot.config().nextNode().orElse("")))
                    .findFirst().orElse(null);

            if (nextNode == null) {
                throw new Exception("Next node not found");
            }

            state.withHumanFeedback(
                    new OverAllState.HumanFeedback(createFeedBack(agentEvent, agentRuntimeContext, nextNode), ""));

            if (agentEvent != null && agentEvent.isStream()) {
                AsyncGenerator<NodeOutput> asyncGenerator = agentRuntimeContext.getCompiledGraph()
                        .streamFromInitialNode(state, runnableConfig);

                invokeGraphStream(agentRuntimeContext, agentEvent, asyncGenerator);
            } else {
                agentRuntimeContext.getCompiledGraph()
                        .invoke(state, runnableConfig);
            }

        }

        return agentEvent;

    }

    private void invokeGraphStream(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            AsyncGenerator<NodeOutput> asyncGenerator) {
        Executors.newSingleThreadExecutor().submit(() -> {
            asyncGenerator.forEachAsync(output -> {
                try {
                    if (output instanceof StreamingOutput streamingOutput) {

                        eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                                AgentGraphInvokeStreamContent.builder().isComplete(false).isError(false)
                                        .content(streamingOutput.chunk()).build());

                    }

                } catch (Exception e) {
                    eventListener.onError(agent, agentEvent, agentRuntimeContext, e);
                }

            }).thenAccept(v -> {

                eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                        AgentGraphInvokeStreamContent.builder().isComplete(true).isError(false)
                                .build());

            }).exceptionally(e -> {
                eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                        AgentGraphInvokeStreamContent.builder().isComplete(false).isError(true)
                                .build());
                return null;
            });
        });
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    @Override
    public <T extends IGraphState> void onGraphPartApplield(IGraphPart graphPart,
            IUpdatedGraphState<T> updatedGraphState,
            IGraphState graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) {
        if (agentEvent.isStream() && updatedGraphState instanceof IUpdatedGraphStateValue updatedGraphStateValue) {

            Map<String, Object> stateMap = updatedGraphStateValue.exec();
            eventListener.onInvokeStream(agent, agentEvent, agentRuntimeContext,
                    AgentGraphInvokeStreamContent.builder().isComplete(false).isError(false)
                            .content(stateMap.values().stream().map(Object::toString).collect(Collectors.joining("\n")))
                            .build());

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) {
        putInput(input, agentRuntimeContext, agentEvent, (T) agentRuntimeContext.getGraphState());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode) {
        putFeedBack(feedBack, agentRuntimeContext, agentEvent, graphNode, (T) agentRuntimeContext.getGraphState());
    }

    @SuppressWarnings("unchecked")
    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode) {

        IAgentResponse agentResponse = createOutput(agentRuntimeContext, agentEvent, graphNode,
                (T) agentRuntimeContext.getGraphState());

        return agentResponse;
    }

    public abstract void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, T graphState);

    public abstract void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, T graphState);

    public abstract IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, T graphState);

}
