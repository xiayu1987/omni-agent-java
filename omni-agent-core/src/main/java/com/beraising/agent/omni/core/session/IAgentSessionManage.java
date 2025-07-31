package com.beraising.agent.omni.core.session;

import java.util.List;

public interface IAgentSessionManage {

    List<IAgentSession> getAgentSessions();

    List<IAgentSession> getAgentSessionsByUser(IAgentSessionUser user);

    IAgentSession getAgentSessionById(String sessionId);

    IAgentSession createAgentSession(IAgentSessionUser user);

    IAgentSession createAndAddAgentSession(IAgentSessionUser user);

    IAgentSessionPart getCurrentSessionPart(IAgentSession agentSession);

    void clearAllSessions();

}
