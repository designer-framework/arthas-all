package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.MethodInvokeWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-27 14:45
 */
@Slf4j
class BeanDefinitionRegistryUtils {

    public static final String ENABLED = "${spring.agent.flame-graph.high-precision}";

    public static void registry(BeanDefinitionRegistry registry, MethodInvokeWatchProperties methodInvokeWatchProperties) {
        String beanName = getBeanName(methodInvokeWatchProperties);

        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Repeated method call statistics, ignored: {}", methodInvokeWatchProperties.getMethodInfo().getFullyQualifiedMethodName());
            return;
        }

        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(methodInvokeWatchProperties.getPointcutAdvisor());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(methodInvokeWatchProperties.getMethodInfo());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(methodInvokeWatchProperties.getCanRetransform());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(methodInvokeWatchProperties.getInterceptor());
        //
        registry.registerBeanDefinition(beanName, methodInvokeAdviceHandlerBuilder.getBeanDefinition());
    }

    private static String getBeanName(MethodInvokeWatchProperties methodInvokeWatchProperties) {
        return SimpleMethodInvokePointcutAdvisor.class.getSimpleName() + "." + methodInvokeWatchProperties;
    }

}
