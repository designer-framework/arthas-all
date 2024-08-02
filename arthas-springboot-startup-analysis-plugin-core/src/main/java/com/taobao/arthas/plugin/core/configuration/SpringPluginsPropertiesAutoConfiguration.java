package com.taobao.arthas.plugin.core.configuration;


import com.taobao.arthas.plugin.core.properties.AgentPluginsProperties;
import com.taobao.arthas.plugin.core.properties.ScanningPluginProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = AgentPluginsProperties.class)
public class SpringPluginsPropertiesAutoConfiguration {

    @Bean
    ScanningPluginProperties scanningPluginProperties(AgentPluginsProperties agentPluginsProperties) {
        return agentPluginsProperties.getScanning();
    }

}
