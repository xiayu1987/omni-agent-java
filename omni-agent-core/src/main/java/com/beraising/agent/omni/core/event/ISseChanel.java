package com.beraising.agent.omni.core.event;

public interface ISseChanel {

    void tryEmitNext(IAgentEvent agentEvent);

    void tryEmitError(Throwable error);

    void tryEmitComplete();

}
