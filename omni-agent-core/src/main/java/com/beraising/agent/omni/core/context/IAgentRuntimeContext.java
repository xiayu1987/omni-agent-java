package com.beraising.agent.omni.core.context;

import java.util.List;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public interface IAgentRuntimeContext extends IAgentContext {

    IGraphState getGraphState();

    void setGraphState(IGraphState graphState);

    List<IAgentEvent> getAgentEvents();

    void setAgentEvents(List<IAgentEvent> agentEvents);

    IAgent getAgent();

    void setAgent(IAgent agent);

    CompiledGraph getCompiledGraph();

    void setCompiledGraph(CompiledGraph compiledGraph);

    String getAgentSessionID();

    void setAgentSessionID(String agentSessionID);

    String getAgentRuntimeContextID();

    void setAgentRuntimeContextID(String agentRuntimeContextID);

    String getAgentName();

    void setAgentName(String agentName);

    boolean isEnd();

    void setIsEnd(boolean isEnd);

}
