package com.beraising.agent.omni.core.agents;

public interface IAgentListener {

    void beforeInvoke() throws Exception;

    void afterInvoke() throws Exception;

}
