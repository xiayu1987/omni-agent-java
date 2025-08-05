package com.beraising.agent.omni.core.graph.router.nodes;

import java.util.stream.Collectors;

import com.beraising.agent.omni.core.graph.GraphNodeBase;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.router.state.RouterState;

public class RouteNode extends GraphNodeBase<RouterState> {

    public RouteNode(IGraph graph) {
        super(graph);
    }

    @Override
    public IUpdatedGraphState<RouterState> apply(RouterState state) throws Exception {

        String content = getChatClient().prompt().user(state.getUserQuery()).toolCallbacks(

                getAgentStaticContext().getAgentRegistry().getAgentMap().entrySet().stream().map(entry -> {
                    return entry.getValue().asToolCallback();
                }).collect(Collectors.toList())

        ).call().content();

        return state.getUpdatedRouterResult(content);
    }

}
