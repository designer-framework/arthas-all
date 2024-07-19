package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.constants.ProfilingLifeCycleOrdered;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionShutdownHookPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final AnnotationConfigApplicationContext annotationConfigApplicationContext;

    public ArthasExtensionShutdownHookPostProcessor(AnnotationConfigApplicationContext annotationConfigApplicationContext) {
        this.annotationConfigApplicationContext = annotationConfigApplicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //
        BeanDefinitionBuilder shutdownContainerHookBuild = BeanDefinitionBuilder
                .genericBeanDefinition(ProfilingLifeCycle.class, () -> new ShutdownContainer(annotationConfigApplicationContext));
        registry.registerBeanDefinition("arthas.extension.shutdown." + ProfilingLifeCycle.class.getName(), shutdownContainerHookBuild.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @AllArgsConstructor
    private static class ShutdownContainer implements ProfilingLifeCycle, Ordered {

        private final AnnotationConfigApplicationContext annotationConfigApplicationContext;

        @Override
        public void stop() {
            annotationConfigApplicationContext.close();
        }

        @Override
        public int getOrder() {
            return ProfilingLifeCycleOrdered.STOP_CONTAINER;
        }

    }

}
