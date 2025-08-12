package com.beraising.agent.omni.mcp.service.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormField {

    private String fieldName;
    private EFieldType fieldType;

}
