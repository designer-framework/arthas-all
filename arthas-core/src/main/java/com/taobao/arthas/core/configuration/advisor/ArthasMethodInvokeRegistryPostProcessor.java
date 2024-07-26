package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.ArthasMethodTraceProperties;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
@Setter
public class ArthasMethodInvokeRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

    static void registry(BeanDefinitionRegistry registry, ArthasMethodTraceProperties.ClassMethodDesc classMethodDesc) {
        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(SimpleMethodInvokePointcutAdvisor.class);
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(classMethodDesc.getMethodInfo());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(classMethodDesc.getCanRetransform());
        //
        registry.registerBeanDefinition(
                SimpleMethodInvokePointcutAdvisor.class.getSimpleName() + "." + classMethodDesc
                , methodInvokeAdviceHandlerBuilder.getBeanDefinition()
        );
    }

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see SimpleMethodInvokePointcutAdvisor
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Binder.get(environment)
                //将配置绑定到对象上
                .bind("spring.profiling.trace", ArthasMethodTraceProperties.class)
                .ifBound(arthasMethodTraceProperties -> {

                    //将性能分析Bean的Definition注入到容器中
                    for (ArthasMethodTraceProperties.ClassMethodDesc classMethodDesc : arthasMethodTraceProperties.getMethods()) {
                        registry(registry, classMethodDesc);
                    }

                });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
