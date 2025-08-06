package com.beraising.agent.omni.core.graph.router.nodes;

import java.util.stream.Collectors;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.GraphNodeBase;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.router.state.RouterState;

public class RouteNode extends GraphNodeBase<RouterState> {

    public RouteNode(IGraph graph) {
        super(graph);
    }

    @Override
    public IUpdatedGraphState<RouterState> apply(RouterState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {
        String content = getChatClient().prompt().system("session_id:" + graphState.getAgentSessionID())
                .user(graphState.getUserInput())
                .toolCallbacks(

                        getAgentStaticContext().getAgentRegistry().getAgentMap().entrySet().stream().map(entry -> {
                            IAgentEvent agentEventCopy = agentEvent.copy();
                            agentEventCopy.setAgentSessionID(null);
                            agentEventCopy.setParentAgentSessionID(agentEvent.getAgentSessionID());
                            return entry.getValue().asToolCallback(agentEventCopy);
                        }).collect(Collectors.toList())

                ).call().content();

        return graphState.getUpdatedRouterResult(content);
    }

}
