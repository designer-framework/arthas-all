package com.taobao.arthas.core.spring.utils;

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
    public static <T> void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry, Class<T> candidateSpiClass) throws ClassNotFoundException {
        //
        List<String> candidateClassNames = SpringFactoriesLoader.loadFactoryNames(candidateSpiClass, candidateSpiClass.getClassLoader());

        //注入容器中
        for (String candidateClassName : candidateClassNames) {

            Class<?> candidateClass = Class.forName(candidateClassName);
            BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(candidateClass);
            definition.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(candidateClass.getSimpleName(), definition.getBeanDefinition());

        }

    }

}
