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
@ConfigurationProperties(prefix = "spring.profiling")
public class ArthasConfigProperties {

    @NestedConfigurationProperty
    private ArthasOutputProperties output;

    @NestedConfigurationProperty
    private ArthasClassLoaderProperties classLoaders;

    @NestedConfigurationProperty
    private ArthasMethodTraceProperties trace;

    @NestedConfigurationProperty
    private ArthasThreadTraceProperties thread;

}
