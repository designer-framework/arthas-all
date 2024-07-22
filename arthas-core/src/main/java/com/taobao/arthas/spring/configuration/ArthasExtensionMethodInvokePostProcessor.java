package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.spring.profiling.invoke.SpringMethodInvokeAdviceHandler;
import com.taobao.arthas.spring.properties.ArthasTraceProperties;
import com.taobao.arthas.spring.vo.ClassMethodInfo;
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
     * @see MatchCandidate
     * @see com.taobao.arthas.core.spring.handler.InvokeAdviceHandler
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Binder.get(environment)
                .bind("spring.profiling.invoke.trace", ArthasTraceProperties.class)
                .ifBound(arthasTraceProperties -> {

                    //将配置绑定到对象上
                    for (ClassMethodInfo classMethodInfo : arthasTraceProperties.traceMethods()) {

                        //注入到容器中
                        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder
                                .genericBeanDefinition(SpringMethodInvokeAdviceHandler.class);
                        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(classMethodInfo);
                        //
                        registry.registerBeanDefinition(
                                SpringMethodInvokeAdviceHandler.class.getSimpleName() + "." + classMethodInfo.getFullyQualifiedMethodName()
                                , methodInvokeAdviceHandlerBuilder.getBeanDefinition()
                        );

                    }

                });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
