package com.beraising.agent.omni.core.event;

import java.util.Map;

import com.beraising.agent.omni.core.agents.EAgentRequestType;

public interface IAgentRequest {

    EAgentRequestType getRequestType();

    void setRequestType(EAgentRequestType requestType);

    String getText();

    void setText(String text);

    Map<String, Object> getData();

    void setData(Map<String, Object> data);

    IAgentRequest copy();

}
