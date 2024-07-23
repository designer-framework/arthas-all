package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.spring.properties.ArthasClassLoaderProperties;
import com.taobao.arthas.spring.properties.ArthasConfigProperties;
import com.taobao.arthas.spring.properties.ArthasMethodTraceProperties;
import com.taobao.arthas.spring.properties.ArthasThreadTraceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ArthasConfigProperties.class)
public class ArthasPropertiesConfiguration {

    @Bean
    public ArthasMethodTraceProperties arthasConfigProperties(ArthasConfigProperties arthasConfigProperties) {
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
