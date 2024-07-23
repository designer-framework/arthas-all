package com.taobao.arthas.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
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

    @NestedConfigurationProperty
    private ArthasClassLoaderProperties classLoaders;

    @NestedConfigurationProperty
    private ArthasMethodTraceProperties trace;

    @NestedConfigurationProperty
    private ArthasThreadTraceProperties thread;

}
