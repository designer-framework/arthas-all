package com.taobao.arthas.core.configuration.env;

import com.taobao.arthas.core.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AgentConfigProperties.class)
public class AgentPropertiesAutoConfiguration {

    @Bean
    public AgentOutputProperties arthasOutputProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getOutput();
    }

    @Bean
    public AgentMethodTraceProperties arthasMethodTraceProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getTrace();
    }

    @Bean
    public AgentFlameGraphProperties agentFlameGraphProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getFlameGraph();
    }

    @Bean
    public AgentClassLoaderProperties arthasClassLoaderProperties(AgentConfigProperties agentConfigProperties) {
        return agentConfigProperties.getClassLoaders();
    }

}
