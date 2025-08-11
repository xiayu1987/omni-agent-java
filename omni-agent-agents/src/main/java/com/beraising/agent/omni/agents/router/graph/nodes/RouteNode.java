package com.beraising.agent.omni.agents.router.graph.nodes;

import java.util.stream.Collectors;

import com.beraising.agent.omni.agents.router.graph.state.RouterState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public class RouteNode extends GraphNodeBase<RouterState> {

    public RouteNode(String name, IAgentGraph graph) {
        super(name, graph);
    }

    @Override
    public IUpdatedGraphState<RouterState> apply(RouterState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {
        String content = getChatClient().prompt().system("session_id:" + graphState.getAgentSessionID())
                .user(graphState.getUserInput())
                .toolCallbacks(

                        getAgentStaticContext().getAgentRegistry().getAgentMap().entrySet().stream().map(entry -> {
                            return entry.getValue().asToolCallback(agentEvent);
                        }).collect(Collectors.toList())

                ).call().content();

        return graphState.getUpdatedRouterResult(content);
    }

}
