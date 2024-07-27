package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.AgentMethodTraceProperties;
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

    public static void registry(BeanDefinitionRegistry registry, AgentMethodTraceProperties.ClassMethodDesc classMethodDesc) {
        String beanName = getBeanName(classMethodDesc);

        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Repeated method call statistics, ignored: {}", classMethodDesc.getMethodInfo().getFullyQualifiedMethodName());
            return;
        }

        BeanDefinitionBuilder methodInvokeAdviceHandlerBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(SimpleMethodInvokePointcutAdvisor.class);
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(classMethodDesc.getMethodInfo());
        methodInvokeAdviceHandlerBuilder.addConstructorArgValue(classMethodDesc.getCanRetransform());
        //
        registry.registerBeanDefinition(beanName, methodInvokeAdviceHandlerBuilder.getBeanDefinition());
    }

    private static String getBeanName(AgentMethodTraceProperties.ClassMethodDesc classMethodDesc) {
        return SimpleMethodInvokePointcutAdvisor.class.getSimpleName() + "." + classMethodDesc;
    }

}
