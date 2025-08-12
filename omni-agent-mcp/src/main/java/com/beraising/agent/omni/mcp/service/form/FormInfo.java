package com.beraising.agent.omni.mcp.service.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormInfo {

    private String formName;
    private EFormType formType;
    private List<FormField> formFields;

}
