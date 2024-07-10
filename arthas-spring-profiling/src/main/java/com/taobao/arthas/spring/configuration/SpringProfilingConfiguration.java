package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.AdviceListener;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.spy.SpyExtensionApi;
import com.taobao.arthas.spring.advisor.SpringAdviceListener;
import com.taobao.arthas.spring.profiling.bean.SpringBeanCreateAdviceHandler;
import com.taobao.arthas.spring.properties.ArthasProperties;
import com.taobao.arthas.spring.spy.SpringSpyExtensionApiImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 23:16
 */
@Configuration
public class SpringProfilingConfiguration {

    @Bean
    ArthasProperties arthasProperties() {
        return new ArthasProperties();
    }

    @Bean
    SpyExtensionApi spyExtensionApi(List<AdviceListener> springAdviceListeners) {
        return new SpringSpyExtensionApiImpl(springAdviceListeners);
    }

    @Bean
    SpringAdviceListener springAdviceListener(InvokeAdviceHandler invokeAdviceHandler, ArthasProperties arthasProperties) {
        return new SpringAdviceListener(invokeAdviceHandler, arthasProperties);
    }

    @Bean
    InvokeAdviceHandler springBeanCreateAdviceHandler(Environment environment) {
        return new SpringBeanCreateAdviceHandler();
    }

}
