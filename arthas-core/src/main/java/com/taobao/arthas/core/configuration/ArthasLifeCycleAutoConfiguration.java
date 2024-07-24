package com.taobao.arthas.core.configuration;

import com.taobao.arthas.core.profiling.hook.ArthasExtensionShutdownHookPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
public class ArthasLifeCycleAutoConfiguration {

    @Bean
    ArthasExtensionShutdownHookPostProcessor arthasExtensionShutdownHookPostProcessor() {
        return new ArthasExtensionShutdownHookPostProcessor();
    }

}
