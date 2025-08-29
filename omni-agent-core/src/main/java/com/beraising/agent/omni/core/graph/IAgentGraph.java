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

public interface IAgentGraph {

        void init(IAgent agent, IEventListener eventListener,
                        IAgentGraphListener agentGraphListener) throws Exception;

        IAgentEvent invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception;

        StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception;

        void setAgent(IAgent agent);

        IAgent getAgent();

        void setAgentGraphListener(IAgentGraphListener agentGraphListener);

        IAgentGraphListener getAgentGraphListener();

        void setEventListener(IEventListener eventListener);

        IEventListener getEventListener();

        void setGraphNodes(List<IGraphNode> graphNodes);

        List<IGraphNode> getGraphNodes();

        void setGraphEdges(List<IGraphEdge> graphEdges);

        List<IGraphEdge> getGraphEdges();

        IGraphState newGraphState();

        <T extends IGraphState> void onGraphPartApplield(IGraphPart graphPart, IUpdatedGraphState<T> updatedGraphState,
                        IGraphState graphState, IAgentRuntimeContext agentRuntimeContext,
                        IAgentEvent agentEvent);

        default Map<String, Object> createInput(IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext) {
                Map<String, Object> inputMap = new HashMap<>();
                inputMap.put(IGraphState.getAgentSessionIDKey(), agentRuntimeContext.getAgentSessionID());
                inputMap.put(IGraphState.getAgentRuntimeContextIDKey(), agentRuntimeContext.getAgentRuntimeContextID());
                putInput(inputMap, agentRuntimeContext, agentEvent);
                return inputMap;
        }

        default Map<String, Object> createFeedBack(IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        IGraphNode graphNode) {
                Map<String, Object> inputMap = new HashMap<>();
                putFeedBack(inputMap, agentRuntimeContext, agentEvent, graphNode);

                return inputMap;
        }

        IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
                        IGraphNode graphNode);

        void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
                        IAgentEvent agentEvent);

        void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
                        IAgentEvent agentEvent, IGraphNode graphNode);

}
