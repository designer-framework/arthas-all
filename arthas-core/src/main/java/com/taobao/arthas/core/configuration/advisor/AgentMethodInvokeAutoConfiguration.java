package com.taobao.arthas.core.configuration.advisor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AgentMethodInvokeAutoConfiguration {

    @Bean
    AgentMethodInvokeRegistryPostProcessor arthasMethodInvokePostProcessor() {
        return new AgentMethodInvokeRegistryPostProcessor();
    }

}
