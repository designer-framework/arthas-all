package com.taobao.arthas.core.configuration.extension;

import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.core.spy.SpyExtensionApiImpl;
import com.taobao.arthas.core.spy.SpyImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.arthas.SpyAPI;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class AgentExtensionAutoConfiguration {

    @Bean
    SpyAPI.AbstractSpy abstractSpy(SpyExtensionApi spyExtensionApi) {
        return new SpyImpl(spyExtensionApi);
    }

    @Bean
    SpyExtensionApi spyExtensionApi(List<PointcutAdvisor> pointcutAdvisors) {
        return new SpyExtensionApiImpl(pointcutAdvisors);
    }

}
