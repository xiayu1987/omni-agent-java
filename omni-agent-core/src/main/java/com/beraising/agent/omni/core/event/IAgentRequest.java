package com.beraising.agent.omni.core.event;

public interface IAgentRequest {

    EAgentRequestType getRequestType();

    void setRequestType(EAgentRequestType requestType);

    String getRequestData();

    void setRequestData(String requestData);

    IAgentRequest copy();

}
