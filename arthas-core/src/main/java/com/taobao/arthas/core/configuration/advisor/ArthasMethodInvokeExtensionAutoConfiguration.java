package com.taobao.arthas.core.configuration.advisor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ArthasMethodInvokeExtensionAutoConfiguration {

    @Bean
    ArthasMethodInvokeRegistryPostProcessor arthasMethodInvokePostProcessor() {
        return new ArthasMethodInvokeRegistryPostProcessor();
    }

}
