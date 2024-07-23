package com.taobao.arthas.core.configuration;

import com.taobao.arthas.api.pointcut.ClassMethodMatchPointcut;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.ArthasMethodTraceProperties;
import com.taobao.arthas.core.vo.ClassMethodInfo;
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
public class ArthasExtensionMethodInvokePostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    @Setter
    private Environment environment;

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see ClassMethodMatchPointcut
     * @see SimpleMethodInvokePointcutAdvisor
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Binder.get(environment)
                //将配置绑定到对象上
                .bind("spring.profiling.trace", ArthasMethodTraceProperties.class)
                .ifBound(arthasMethodTraceProperties -> {

                    //将性能分析Bean的Definition注入到容器中
                    for (ClassMethodInfo classMethodInfo : arthasMethodTraceProperties.traceMethods()) {

                        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder
                                .genericBeanDefinition(SimpleMethodInvokePointcutAdvisor.class);
                        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(classMethodInfo);
                        //
                        registry.registerBeanDefinition(
                                SimpleMethodInvokePointcutAdvisor.class.getSimpleName() + "." + classMethodInfo.getFullyQualifiedMethodName()
                                , methodInvokeAdviceHandlerBuilder.getBeanDefinition()
                        );

                    }

                });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
