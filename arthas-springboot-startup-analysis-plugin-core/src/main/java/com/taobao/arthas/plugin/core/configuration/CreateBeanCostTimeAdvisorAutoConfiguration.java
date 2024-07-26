package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.configuration.advisor.AdvisorUtils;
import com.taobao.arthas.plugin.core.profiling.bean.*;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CreateBeanCostTimeAdvisorAutoConfiguration {

    /**
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    @Bean
    public SpringBeanCreationPointcutAdvisor springBeanCreationPointcutAdvisor(SpringAgentStatisticsVO springAgentStatisticsVO) {
        return AdvisorUtils.build(
                new SpringBeanCreationPointcutAdvisor(springAgentStatisticsVO)
                , "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])");
    }

    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Bean
    public SpringBeanAopProxyPointcutAdvisor springBeanAopProxyPointcutAdvisor(InitializingSingletonsStep2PointcutAdvisor initializingSingletonsStep2PointcutAdvisor) {
        return AdvisorUtils.build(
                new SpringBeanAopProxyPointcutAdvisor()
                , "org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)");
    }

    /**
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()
     */
    @Bean
    public InitializingSingletonsStep1PointcutAdvisor initializingSingletonsStep1PointcutAdvisor() {
        return AdvisorUtils.build(
                new InitializingSingletonsStep1PointcutAdvisor()
                , "org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()");
    }

    /**
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    @Bean
    public InitializingSingletonsStep2PointcutAdvisor initializingSingletonsStep2PointcutAdvisor(InitializingSingletonsStep1PointcutAdvisor initializingSingletonsStep1PointcutAdvisor) {
        return AdvisorUtils.build(
                new InitializingSingletonsStep2PointcutAdvisor(initializingSingletonsStep1PointcutAdvisor)
                , "org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(java.lang.String)");
    }

    /**
     * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
     */
    @Bean
    public InitializingSingletonsStep3PointcutAdvisor initializingSingletonsStep3PointcutAdvisor(InitializingSingletonsStep2PointcutAdvisor initializingSingletonsStep2PointcutAdvisor) {
        return AdvisorUtils.build(
                new InitializingSingletonsStep3PointcutAdvisor(initializingSingletonsStep2PointcutAdvisor)
                , "**#afterSingletonsInstantiated()");
    }

}
