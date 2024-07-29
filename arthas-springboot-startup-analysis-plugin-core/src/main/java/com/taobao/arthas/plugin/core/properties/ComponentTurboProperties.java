package com.taobao.arthas.plugin.core.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @see com.taobao.arthas.plugin.core.condition.OnTurboCondition
 */
@Data
@ConfigurationProperties(prefix = "spring.agent.turbo")
public class ComponentTurboProperties {

    private boolean enabledByDefault;

    @NestedConfigurationProperty
    private Enabled swagger;

    @NestedConfigurationProperty
    private Enabled openFeign;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Enabled {

        static final Enabled disabled = new Enabled(false);

        private boolean enabled;

    }

}
