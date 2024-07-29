package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * list.{ ?#this == 99999}
 *
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 */
@Configuration(proxyBeanMethods = false)
public class SpringComponentTurboAutoConfiguration {

    @Bean
    @ConditionalOnTurboCondition(pluginName = "open-feign")
    public AgentLifeCycleHook feignClientsCreatorPointcutAdvisor() {
        return new AgentLifeCycleHook() {
        };
    }

    @Bean
    @ConditionalOnTurboCondition(pluginName = "swagger")
    public AgentLifeCycleHook swaggerTubroAgentLifeCycleHook() {
        return new AgentLifeCycleHook() {
            private static final String SPRINGFOX_DOCUMENTATION_AUTO_STARTUP = "springfox.documentation.auto-startup";

            @Override
            public void start() {
                System.setProperty(SPRINGFOX_DOCUMENTATION_AUTO_STARTUP, "false");
            }

        };
    }

}
