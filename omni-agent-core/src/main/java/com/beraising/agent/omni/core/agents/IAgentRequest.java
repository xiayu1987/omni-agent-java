package com.beraising.agent.omni.core.agents;

import java.util.Map;

public interface IAgentRequest {

    EAgentRequestType getRequestType();

    void setRequestType(EAgentRequestType requestType);

    String getText();

    void setText(String text);

    Map<String, Object> getData();

    void setData(Map<String, Object> data);

}
