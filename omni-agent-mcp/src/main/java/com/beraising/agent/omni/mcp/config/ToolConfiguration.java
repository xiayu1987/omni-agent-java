package com.beraising.agent.omni.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.beraising.agent.omni.mcp.service.form.FormService;

@Configuration
public class ToolConfiguration {

    @Bean
    public ToolCallbackProvider formTools(FormService formService) {
        return MethodToolCallbackProvider.builder().toolObjects(formService).build();
    }

}
