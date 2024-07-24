package com.taobao.arthas.core.configuration;

import com.taobao.arthas.core.properties.ArthasClassLoaderProperties;
import com.taobao.arthas.core.properties.ArthasConfigProperties;
import com.taobao.arthas.core.properties.ArthasMethodTraceProperties;
import com.taobao.arthas.core.properties.ArthasThreadTraceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ArthasConfigProperties.class)
public class ArthasPropertiesAutoConfiguration {

    @Bean
    public ArthasConfigProperties arthasConfigProperties() {
        return new ArthasConfigProperties();
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
