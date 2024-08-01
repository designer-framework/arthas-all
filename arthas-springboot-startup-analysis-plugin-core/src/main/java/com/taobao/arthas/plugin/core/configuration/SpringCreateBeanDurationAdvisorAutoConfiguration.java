package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.configuration.advisor.AdvisorUtils;
import com.taobao.arthas.plugin.core.profiling.bean.InitializingSingletonsPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.bean.SpringBeanAopProxyPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.bean.SpringBeanCreationPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.bean.SpringInitAnnotationBeanPointcutAdvisor;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SpringCreateBeanDurationAdvisorAutoConfiguration {

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
    public SpringBeanAopProxyPointcutAdvisor springBeanAopProxyPointcutAdvisor() {
        return AdvisorUtils.build(
                new SpringBeanAopProxyPointcutAdvisor()
                , "org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)");
    }

    /**
     * 对private类插装可以得到更加详细的结果, 但不稳定性较强。 所以选择拦截该方法
     * <p>
     * 不会统计加载时长小于10ms的Bean
     *
     * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)
     */
    @Bean
    public SpringInitAnnotationBeanPointcutAdvisor initAnnotationBeanPointcutAdvisor() {
        return AdvisorUtils.build(
                new SpringInitAnnotationBeanPointcutAdvisor()
                //, "org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)"
                , "org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor$LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)"
                , SpringInitAnnotationBeanPointcutAdvisor.InitMethodSpyInterceptorApi.class
        );
    }

    @Bean
    public InitializingSingletonsPointcutAdvisor initializingSingletonsPointcutAdvisor() {
        return AdvisorUtils.build(
                new InitializingSingletonsPointcutAdvisor()
                , "org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()"
                , InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi.class
        );
    }

}
