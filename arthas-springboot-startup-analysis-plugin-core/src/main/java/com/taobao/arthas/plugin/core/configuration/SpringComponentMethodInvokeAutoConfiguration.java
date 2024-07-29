package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;
import com.taobao.arthas.plugin.core.advisor.ApolloCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.advisor.FeignClientsCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.advisor.SwaggerCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.profiling.component.ComponentInitializedListener;
import com.taobao.arthas.plugin.core.properties.ComponentTurboProperties;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * list.{ ?#this == 99999}
 *
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 */
@Configuration(proxyBeanMethods = false)
@EnabledMethodInvokeWatch({
        //扫包耗时
        @MethodInvokeWatch("org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)"),
})
@EnableConfigurationProperties(value = ComponentTurboProperties.class)
public class SpringComponentMethodInvokeAutoConfiguration {

    @Bean
    ComponentInitializedListener componentInitializedListener(SpringAgentStatistics springAgentStatistics) {
        return new ComponentInitializedListener(springAgentStatistics);
    }

    /**
     * FeignClient耗时
     *
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget(), getObject()
     */
    @Bean
    FeignClientsCreatorPointcutAdvisor feignClientsCreatorPointcutAdvisor() {
        return new FeignClientsCreatorPointcutAdvisor(
                SpringComponentEnum.SWAGGER
                , ClassMethodInfo.create("org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget()")
                , FeignClientsCreatorPointcutAdvisor.FeignClientSpyInterceptorApi.class
        );
    }

    /**
     * @return
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#start()
     */
    @Bean
    SwaggerCreatorPointcutAdvisor swaggerCreatorPointcutAdvisor() {
        return new SwaggerCreatorPointcutAdvisor(
                SpringComponentEnum.SWAGGER
                , ClassMethodInfo.create("springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#start()")
                , SimpleSpyInterceptorApi.class
        );
    }

    /**
     * Apollo配置加载耗时
     *
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    @Bean
    ApolloCreatorPointcutAdvisor apolloCreatorPointcutAdvisor() {
        return new ApolloCreatorPointcutAdvisor(
                SpringComponentEnum.APOLLO
                , ClassMethodInfo.create("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
                , SimpleSpyInterceptorApi.class
        );
    }

}
