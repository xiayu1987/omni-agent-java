package com.beraising.agent.omni.core.session;

import java.util.List;

public interface IAgentSessionManage {

    List<IAgentSession> getAgentSessions();

    IAgentSession getAgentSessionById(String sessionId);

    IAgentSession createAgentSession();

    IAgentSession createAndAddAgentSession();

    IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession);

    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    void clearAllSessions();

}
