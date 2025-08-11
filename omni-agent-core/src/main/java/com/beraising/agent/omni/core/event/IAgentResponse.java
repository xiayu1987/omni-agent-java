package com.beraising.agent.omni.core.event;

public interface IAgentResponse {

    EAgentResponseType getResponseType();

    void setResponseType(EAgentResponseType responseType);

    String getResponseData();

    void setResponseData(String responseData);

    IAgentResponse copy();

}
