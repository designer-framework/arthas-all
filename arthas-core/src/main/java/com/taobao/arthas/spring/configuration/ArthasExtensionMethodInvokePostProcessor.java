package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.spring.profiling.invoke.SpringMethodInvokeAdviceHandler;
import com.taobao.arthas.spring.properties.ArthasProperties;
import com.taobao.arthas.spring.vo.ClassMethodInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionMethodInvokePostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final ArthasProperties arthasProperties;

    public ArthasExtensionMethodInvokePostProcessor(ArthasProperties arthasProperties) {
        this.arthasProperties = arthasProperties;
    }

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see MatchCandidate
     * @see com.taobao.arthas.core.spring.handler.InvokeAdviceHandler
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        //将配置绑定到对象上
        for (ClassMethodInfo classMethodInfo : arthasProperties.traceMethods()) {

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

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
