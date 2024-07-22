package com.taobao.arthas.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.profiling")
public class ArthasConfigProperties {

    private String home;

    private String outputPath;

    private Set<String> enhanceLoaders = new HashSet<>(Collections.singletonList("java.lang.ClassLoader"));

    private Boolean overrideConfig = Boolean.TRUE;

    @NestedConfigurationProperty
    private ArthasTraceProperties trace;

    @NestedConfigurationProperty
    private ThreadProfilingProperties thread;

}
