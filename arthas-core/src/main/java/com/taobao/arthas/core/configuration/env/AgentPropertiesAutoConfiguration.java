package com.taobao.arthas.core.configuration.env;

import com.taobao.arthas.core.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ArthasConfigProperties.class)
public class AgentPropertiesAutoConfiguration {

    @Bean
    public ArthasOutputProperties arthasOutputProperties(ArthasConfigProperties arthasConfigProperties) {
        return arthasConfigProperties.getOutput();
    }

    @Bean
    public ArthasMethodTraceProperties arthasMethodTraceProperties(ArthasConfigProperties arthasConfigProperties) {
        return arthasConfigProperties.getTrace();
    }

    @Bean
    public ArthasThreadTraceProperties arthasThreadTraceProperties(ArthasConfigProperties arthasConfigProperties) {
        return arthasConfigProperties.getThread();
    }

    @Bean
    public ArthasClassLoaderProperties arthasClassLoaderProperties(ArthasConfigProperties arthasConfigProperties) {
        return arthasConfigProperties.getClassLoaders();
    }

}
