package com.taobao.arthas.plugin.core.configuration.trubo;

import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "swagger")
public class SwaggerTurboConfiguration {

    private static final String SPRINGFOX_DOCUMENTATION_AUTO_STARTUP = "springfox.documentation.auto-startup";

    @Bean
    AgentLifeCycleHook swaggerAgentLifeCycleHook() {
        return new AgentLifeCycleHook() {
            @Override
            public void start() {
                System.setProperty(SPRINGFOX_DOCUMENTATION_AUTO_STARTUP, "false");
            }
        };
    }

}
