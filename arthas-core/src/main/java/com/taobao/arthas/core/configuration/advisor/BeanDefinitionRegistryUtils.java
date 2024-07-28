package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.MethodInvokeAdvisor;
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

    public static void registry(BeanDefinitionRegistry registry, MethodInvokeAdvisor methodInvokeAdvisor) {
        String beanName = getBeanName(methodInvokeAdvisor);

        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Repeated method call statistics, ignored: {}", methodInvokeAdvisor.getMethodInfo().getFullyQualifiedMethodName());
            return;
        }

        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(methodInvokeAdvisor.getPointcutAdvisor());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(methodInvokeAdvisor.getMethodInfo());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(methodInvokeAdvisor.getCanRetransform());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(methodInvokeAdvisor.getInterceptor());
        //
        registry.registerBeanDefinition(beanName, methodInvokeAdviceHandlerBuilder.getBeanDefinition());
    }

    private static String getBeanName(MethodInvokeAdvisor methodInvokeAdvisor) {
        return SimpleMethodInvokePointcutAdvisor.class.getSimpleName() + "." + methodInvokeAdvisor;
    }

}
