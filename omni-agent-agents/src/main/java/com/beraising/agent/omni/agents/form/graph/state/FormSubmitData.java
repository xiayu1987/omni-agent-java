package com.beraising.agent.omni.agents.form.graph.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmitData {

    public static final int SUCCESS = 0;
    public static final int BIZERROR = 1;
    public static final int SYSTEMERROR = 2;

    private int resultType;
    private String message;

    

}
