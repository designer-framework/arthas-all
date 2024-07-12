package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.LifeCycle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionSpringPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private final AnnotationConfigApplicationContext annotationConfigApplicationContext;

    private List<Runnable> agentShutdownHooks;

    private Environment environment;

    public ArthasExtensionSpringPostProcessor(AnnotationConfigApplicationContext annotationConfigApplicationContext, List<Runnable> agentShutdownHooks) {
        this.agentShutdownHooks = agentShutdownHooks;
        this.annotationConfigApplicationContext = annotationConfigApplicationContext;
    }

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see MatchCandidate
     * @see com.taobao.arthas.core.spring.handler.InvokeAdviceHandler
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        //
        AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
        //
        addShutdownHook(registry);

    }

    private void addShutdownHook(BeanDefinitionRegistry registry) {
        //
        BeanDefinitionBuilder shutdownContainerHookBuild = BeanDefinitionBuilder.genericBeanDefinition(LifeCycle.class, () -> new LifeCycle() {
            @Override
            public void stop() {
                annotationConfigApplicationContext.close();
            }
        });
        registry.registerBeanDefinition("arthas.extension.shutdown." + LifeCycle.class.getName(), shutdownContainerHookBuild.getBeanDefinition());

        BeanDefinitionBuilder shutdownAgentHookBuilder = BeanDefinitionBuilder.genericBeanDefinition(LifeCycle.class, () -> new LifeCycle() {
            @Override
            public void stop() {
                agentShutdownHooks.forEach(Runnable::run);
            }
        });
        registry.registerBeanDefinition("arthas.agent.shutdown." + LifeCycle.class.getName(), shutdownAgentHookBuilder.getBeanDefinition());


    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
