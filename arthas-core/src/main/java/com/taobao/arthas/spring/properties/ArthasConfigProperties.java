package com.taobao.arthas.spring.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
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

    /**
     * 性能分析jar包所在文件夹路径
     */
    private Resource home;

    /**
     * 日志输出路径
     */
    private String outputPath;

    /**
     * 哪些类加载器需要增强
     */
    private Set<String> enhanceLoaders = new HashSet<>(Collections.singletonList("java.lang.ClassLoader"));

    @NestedConfigurationProperty
    private ArthasTraceProperties trace;

    @NestedConfigurationProperty
    private ThreadProfilingProperties thread;

}
