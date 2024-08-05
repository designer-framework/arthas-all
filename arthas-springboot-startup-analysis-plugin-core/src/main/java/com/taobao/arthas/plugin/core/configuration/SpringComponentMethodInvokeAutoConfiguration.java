package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.profiling.statistics.bean.InitializingSingletonsPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.statistics.bean.SpringBeanAopProxyPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.statistics.bean.SpringInitAnnotationBeanPointcutAdvisor;
import com.taobao.arthas.plugin.core.profiling.component.*;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
        //@MethodInvokeWatch("org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)"),
})
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
                SpringComponentEnum.FEIGN_CLIENT_FACTORY_BEAN
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
    @ConditionalOnMissingBean(ApolloCreatorPointcutAdvisor.class)
    ApolloCreatorPointcutAdvisor apolloCreatorPointcutAdvisor() {
        return new ApolloCreatorPointcutAdvisor(
                SpringComponentEnum.APOLLO_APPLICATION_CONTEXT_INITIALIZER
                , ClassMethodInfo.create("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
        );
    }

    /**
     * Apollo配置加载耗时
     *
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    @Bean
    ApolloLoadNamespacePointcutAdvisor apolloLoadNamespacePointcutAdvisor() {
        return new ApolloLoadNamespacePointcutAdvisor(
                SpringComponentEnum.APOLLO_APPLICATION_CONTEXT_INITIALIZER
                , ClassMethodInfo.create("com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)")
        );
    }

    /**
     * 扫包耗时
     *
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    @Bean
    ClassPathScanningCandidateComponentPointcutAdvisor classPathScanningCandidateComponentPointcutAdvisor(SpringAgentStatistics springAgentStatistics) {
        return new ClassPathScanningCandidateComponentPointcutAdvisor(
                SpringComponentEnum.CLASS_PATH_SCANNING_CANDIDATE
                , ClassMethodInfo.create("org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)")
                , springAgentStatistics
        );
    }

    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Bean
    public SpringBeanAopProxyPointcutAdvisor springBeanAopProxyPointcutAdvisor() {
        return new SpringBeanAopProxyPointcutAdvisor(
                SpringComponentEnum.ABSTRACT_AUTO_PROXY_CREATOR,
                ClassMethodInfo.create("org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)")
        );
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
        return new SpringInitAnnotationBeanPointcutAdvisor(
                SpringComponentEnum.INIT_DESTROY_ANNOTATION_BEAN
                , ClassMethodInfo.create("org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor$LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)")
                , SpringInitAnnotationBeanPointcutAdvisor.InitMethodSpyInterceptorApi.class
        );
    }

    @Bean
    public InitializingSingletonsPointcutAdvisor initializingSingletonsPointcutAdvisor() {
        return new InitializingSingletonsPointcutAdvisor(
                SpringComponentEnum.SMART_INITIALIZING_SINGLETON
                , ClassMethodInfo.create("org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()")
                , InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi.class
        );
    }

}
