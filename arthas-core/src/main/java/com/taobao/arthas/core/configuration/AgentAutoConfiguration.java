package com.taobao.arthas.core.configuration;

import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.core.spy.CompositeSpyAPI;
import com.taobao.arthas.core.spy.SpyExtensionApiImpl;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.arthas.SpyAPI;
import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @see com.taobao.arthas.core.configuration.env.AgentPropertiesAutoConfiguration
 * @see com.taobao.arthas.core.configuration.lifecycle.AgentLifeCycleAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
public class AgentAutoConfiguration {

    public static final String INSTRUMENTATION = "com.taobao.arthas.core.configuration.commonInstrumentation";

    @Bean(name = INSTRUMENTATION)
    Instrumentation instrumentation() {
        return ByteBuddyAgent.install();
    }

    @Bean
    SpyAPI.AbstractSpy abstractSpy(List<SpyExtensionApi> spyExtensionApis) {
        return new CompositeSpyAPI(spyExtensionApis);
    }

    @Bean
    SpyExtensionApi spyExtensionApi(List<PointcutAdvisor> pointcutAdvisors) {
        return new SpyExtensionApiImpl(pointcutAdvisors);
    }

}
