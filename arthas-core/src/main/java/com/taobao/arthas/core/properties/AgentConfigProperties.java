package com.taobao.arthas.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@ConfigurationProperties(prefix = "spring.agent")
public class AgentConfigProperties {

    @NestedConfigurationProperty
    private AgentOutputProperties output;

    @NestedConfigurationProperty
    private AgentClassLoaderProperties classLoaders;

    @NestedConfigurationProperty
    private AgentMethodTraceProperties trace;

    @NestedConfigurationProperty
    private AgentFlameGraphProperties flameGraph;

}
