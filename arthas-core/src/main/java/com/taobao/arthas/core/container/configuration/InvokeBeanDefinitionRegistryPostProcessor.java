package com.taobao.arthas.core.container.configuration;

import com.taobao.arthas.core.advisor.AdviceListener;
import com.taobao.arthas.core.container.handler.InvokeAdviceHandler;
import com.taobao.arthas.core.container.interceptor.SpyInterceptors;
import com.taobao.arthas.core.container.matcher.MatchCandidate;
import com.taobao.arthas.core.container.utils.BeanDefinitionRegistryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class InvokeBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see com.taobao.arthas.core.container.matcher.MatchCandidate
     * @see com.taobao.arthas.core.container.handler.InvokeAdviceHandler
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //注入容器中
        BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, InvokeAdviceHandler.class);
        BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, SpyInterceptors.class);
        BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, MatchCandidate.class);
        BeanDefinitionRegistryUtils.postProcessBeanDefinitionRegistry(registry, AdviceListener.class);

    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
