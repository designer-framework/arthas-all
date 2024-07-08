package com.taobao.arthas.core.container.utils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-09 00:05
 */
public class BeanDefinitionRegistryUtils {

    /**
     * @param registry
     */
    public static <T> void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry, Class<T> candidateSpiClass) {
        //
        List<T> candidateClasses = SpringFactoriesLoader.loadFactories(candidateSpiClass, candidateSpiClass.getClassLoader());

        //注入容器中
        for (T candidateClass : candidateClasses) {

            String beanName = candidateClass.getClass().getSimpleName();
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(candidateClass.getClass());
            definition.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(beanName, definition.getBeanDefinition());

        }

    }

}
