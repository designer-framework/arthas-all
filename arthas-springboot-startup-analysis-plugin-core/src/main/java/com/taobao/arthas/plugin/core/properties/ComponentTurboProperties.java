package com.taobao.arthas.plugin.core.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @see com.taobao.arthas.plugin.core.configuration.SpringComponentTurboAutoConfiguration
 */
@Data
@ConfigurationProperties(prefix = "spring.agent.turbo")
public class ComponentTurboProperties {

    private boolean enabledByDefault;

    @NestedConfigurationProperty
    private Enabled swagger;

    @NestedConfigurationProperty
    private Enabled openFeign;

    @NestedConfigurationProperty
    private Enabled apollo;

    @NestedConfigurationProperty
    private Enabled aop;
    
    @NestedConfigurationProperty
    private Enabled forkJoin;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Enabled {

        private boolean enabled;

    }

}
