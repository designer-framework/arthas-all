package com.taobao.arthas.core.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.AdviceListener;
import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.interceptor.SpyInterceptorExtensionApi;
import com.taobao.arthas.profiling.api.spy.SpyExtensionApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.arthas.SpyAPI;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionSpringPostProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see MatchCandidate
     * @see com.taobao.arthas.core.spring.handler.InvokeAdviceHandler
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //注入容器中
        try {

            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) registry;
            postProcessBeanDefinitionRegistry(beanFactory, SpyAPI.AbstractSpy.class);
            postProcessBeanDefinitionRegistry(beanFactory, SpyExtensionApi.class);
            postProcessBeanDefinitionRegistry(beanFactory, SpyInterceptorExtensionApi.class);
            postProcessBeanDefinitionRegistry(beanFactory, AdviceListener.class);
            postProcessBeanDefinitionRegistry(beanFactory, InvokeAdviceHandler.class);

            AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void postProcessBeanDefinitionRegistry(DefaultListableBeanFactory beanFactory, Class<T> candidateSpiClass) throws ClassNotFoundException {
        //

        List<String> candidateClassNames = SpringFactoriesLoader.loadFactoryNames(candidateSpiClass, beanFactory.getBeanClassLoader());

        //注入容器中
        for (String candidateClassName : candidateClassNames) {

            Class<?> candidateClass = Class.forName(candidateClassName);
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(candidateClass);
            definition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
            definition.setScope(BeanDefinition.SCOPE_SINGLETON);
            beanFactory.registerBeanDefinition(candidateClass.getSimpleName(), definition.getBeanDefinition());

        }

    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
