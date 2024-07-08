package com.taobao.arthas.core.container.configuration;

import com.taobao.arthas.core.container.handler.InvokeDispatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class InvokeBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {  //
        List<InvokeDispatcher> invokeListeners = SpringFactoriesLoader.loadFactories(InvokeDispatcher.class, InvokeDispatcher.class.getClassLoader());

        //注入容器中
        for (InvokeDispatcher invokeListener : invokeListeners) {

            String beanName = invokeListener.getClass().getSimpleName();
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(invokeListener.getClass());
            definition.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(beanName, definition.getBeanDefinition());

        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
