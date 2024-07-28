package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import com.taobao.arthas.plugin.core.advisor.FeignClientsCreatorPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
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
public class SpringComponentMethodInvokeAutoConfiguration {

    /**
     * FeignClient耗时
     *
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget()
     */
    @Bean
    public FeignClientsCreatorPointcutAdvisor feignClientsCreatorPointcutAdvisor() {
        return new FeignClientsCreatorPointcutAdvisor();
    }

}
