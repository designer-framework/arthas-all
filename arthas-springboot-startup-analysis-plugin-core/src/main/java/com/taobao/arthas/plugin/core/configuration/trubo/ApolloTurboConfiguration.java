package com.taobao.arthas.plugin.core.configuration.trubo;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.annotation.EnabledInstrument;
import com.taobao.arthas.core.annotation.Retransform;
import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.profiling.component.ApolloCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.component.ApolloCreatorTurboPointcutAdvisor;
import com.taobao.arthas.plugin.core.turbo.constants.TurboConstants;
import com.taobao.arthas.plugin.core.turbo.instrument.ApolloInjector;
import com.taobao.arthas.plugin.core.turbo.instrument.DefaultConfigManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @see com.ctrip.framework.apollo.build.ApolloInjector
 */
@Configuration
@EnabledInstrument({
        @Retransform(className = TurboConstants.ApolloInjector, instrumentClass = ApolloInjector.class),
        @Retransform(className = TurboConstants.DefaultConfigManager, instrumentClass = DefaultConfigManager.class)
})
@ConditionalOnClass(value = ApolloInjector.class)
@ConditionalOnTurboPropCondition(pluginName = "apollo")
public class ApolloTurboConfiguration {

    /**
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    @Bean
    @ConditionalOnMissingBean(ApolloCreatorPointcutAdvisor.class)
    public ApolloCreatorTurboPointcutAdvisor apolloCreatorTurboPointcutAdvisor() {
        return new ApolloCreatorTurboPointcutAdvisor(
                SpringComponentEnum.APOLLO
                , ClassMethodInfo.create("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
        );

    }

}
