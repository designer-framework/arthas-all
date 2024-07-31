package com.taobao.arthas.plugin.core.turbo;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.taobao.arthas.plugin.core.turbo.advisor.ApolloTurboPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "apollo")
public class ApolloTurboAutoConfiguration {

    @Bean
    public ApolloTurboPointcutAdvisor apolloTurboPointcutAdvisor() {
        return new ApolloTurboPointcutAdvisor(
                ClassMethodInfo.create("com.ctrip.framework.apollo.build.ApolloInjector#getInstance(java.lang.Class)")
                , ApolloTurboPointcutAdvisor.TurboSpyInterceptorApi.class
        );
    }

}
