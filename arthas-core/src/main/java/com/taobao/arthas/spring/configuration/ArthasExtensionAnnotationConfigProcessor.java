package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigUtils;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionAnnotationConfigProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see MatchCandidate
     * @see com.taobao.arthas.core.spring.handler.InvokeAdviceHandler
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}