package com.taobao.arthas.core.configuration.env;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

public class MethodInvokeWatchPostProcessor implements BeanDefinitionRegistryPostProcessor, ImportAware {

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        //AnnotationUtils.findAnnotation(importMetadata)
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
