package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.AgentMethodTraceProperties;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-27 14:45
 */
class BeanDefinitionRegistryUtils {

    public static final String ENABLED = "${spring.agent.flame-graph.high-precision}";

    public static void registry(BeanDefinitionRegistry registry, AgentMethodTraceProperties.ClassMethodDesc classMethodDesc) {
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

}
