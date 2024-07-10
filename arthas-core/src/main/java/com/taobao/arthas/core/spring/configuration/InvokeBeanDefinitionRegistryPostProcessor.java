package com.taobao.arthas.core.spring.configuration;

import com.taobao.arthas.core.spring.utils.BeanDefinitionRegistryUtils;
import com.taobao.arthas.profiling.api.advisor.AdviceListener;
import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.interceptor.SpyInterceptorExtensionApi;
import com.taobao.arthas.profiling.api.spy.SpyExtensionApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.arthas.SpyAPI;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class InvokeBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

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
            BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, SpyAPI.AbstractSpy.class);
            BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, SpyExtensionApi.class);
            BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, SpyInterceptorExtensionApi.class);
            BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, AdviceListener.class);
            BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, InvokeAdviceHandler.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
