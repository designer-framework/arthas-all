package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.spring.profiling.invoke.SpringMethodInvokeAdviceHandler;
import com.taobao.arthas.spring.properties.ArthasMethodTraceProperties;
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
     * @see com.taobao.arthas.spring.profiling.invoke.SpringMethodInvokeAdviceHandler
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
