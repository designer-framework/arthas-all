package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;
import com.taobao.arthas.plugin.core.advisor.FeignClientsCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.advisor.SwaggerCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboCondition;
import com.taobao.arthas.plugin.core.properties.ComponentTurboProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        //Apollo配置加载耗时
        @MethodInvokeWatch("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
})
@EnableConfigurationProperties(value = ComponentTurboProperties.class)
public class SpringComponentMethodInvokeAutoConfiguration {


    /**
     * FeignClient耗时
     *
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget(), getObject()
     */
    @Bean
    @ConditionalOnTurboCondition(pluginName = "open-feign")
    public FeignClientsCreatorPointcutAdvisor feignClientsCreatorPointcutAdvisor() {
        return new FeignClientsCreatorPointcutAdvisor(
                ClassMethodInfo.create("org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget()")
                , FeignClientsCreatorPointcutAdvisor.FeignClientSpyInterceptorApi.class
        );
    }

    /**
     * @return
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#start()
     */
    @Bean
    @ConditionalOnTurboCondition(pluginName = "swagger")
    public SwaggerCreatorPointcutAdvisor swaggerCreatorPointcutAdvisor() {
        return new SwaggerCreatorPointcutAdvisor(
                ClassMethodInfo.create("springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#start()")
                , SimpleSpyInterceptorApi.class
        );
    }

}
