package com.beraising.agent.omni.mcp.service.form;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;


@Service
public class FormService {

    @Tool(description = "获取需填写的表单及字段信息")
    public FormInfo getFormInfo(@ToolParam(description = "表单名称") EFormType formType) {
        return FormInfo.builder()
                .formName(formType.getMessage())
                .formType(formType)
                .formFields(List.of(
                        FormField.builder()
                                .fieldName("type")
                                .fieldType(EFieldType.TEXT)
                                .build(),
                        FormField.builder()
                                .fieldName("time")
                                .fieldType(EFieldType.TIME)
                                .build(),
                        FormField.builder()
                                .fieldName("字段2")
                                .fieldType(EFieldType.NUMBER)
                                .build()
                ))
                .build();
    }



}
