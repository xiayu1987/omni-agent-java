package com.beraising.agent.omni.mcp.service.form;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class FormService {

    @Tool(description = "获取需填写的表单及字段信息")
    public String getFormInfo(@ToolParam(description = "表单名称") EFormType formType) {

        if (formType == EFormType.TEST_ERROR) {
            return "{\r\n" + //
                    "  \"isSuccess\": false,\r\n" + //
                    "  \"message\": \"表单服务不可用\"\r\n" + //
                    "}";
        }

        return "\"formInfo\": {\r\n" + //
                "    \"formName\": \"请假\",\r\n" + //
                "    \"formType\": \"LEAVE\",\r\n" + //
                "    \"formFields\": [\r\n" + //
                "      {\r\n" + //
                "        \"fieldName\": \"请假时间\",\r\n" + //
                "        \"fieldType\": \"date\"\r\n" + //
                "      },\r\n" + //
                "      {\r\n" + //
                "        \"fieldName\": \"请假原因\",\r\n" + //
                "        \"fieldType\": \"text\"\r\n" + //
                "      },\r\n" + //
                "      {\r\n" + //
                "        \"fieldName\": \"请假时长\",\r\n" + //
                "        \"fieldType\": \"number\"\r\n" + //
                "      }\r\n" + //
                "    ]\r\n" + //
                "  }";
    }

    @Tool(description = "提交表单数据")
    public String submitForm(@ToolParam(description = "表单名称") EFormType formType,
            @ToolParam(description = "表单数据") String formData) {

        if (formType == EFormType.TEST_ERROR) {
            return "{\r\n" + //
                    "  \"isSuccess\": false,\r\n" + //
                    "  \"message\": \"时间不能为空\"\r\n" + //
                    "}";
        }

        return "{\r\n" + //
                "  \"isSuccess\": true,\r\n" + //
                "}";
    }
}
