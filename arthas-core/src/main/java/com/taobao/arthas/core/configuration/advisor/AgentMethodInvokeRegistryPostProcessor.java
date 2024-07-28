package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.properties.AgentMethodTraceProperties;
import com.taobao.arthas.core.properties.MethodInvokeAdvisor;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
public class AgentMethodInvokeRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

    /**
     * @param registry the bean definition registry used by the application context
     * @throws BeansException
     * @see SimpleMethodInvokePointcutAdvisor
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (Boolean.parseBoolean(environment.resolvePlaceholders(BeanDefinitionRegistryUtils.ENABLED))) {
            return;
        }

        Binder.get(environment)
                //将配置绑定到对象上
                .bind("spring.agent.trace", AgentMethodTraceProperties.class)
                .ifBound(arthasMethodTraceProperties -> {

                    //将性能分析Bean的Definition注入到容器中
                    for (MethodInvokeAdvisor methodInvokeAdvisor : arthasMethodTraceProperties.getMethodInvokeAdvisors()) {
                        BeanDefinitionRegistryUtils.registry(registry, methodInvokeAdvisor);
                    }

                });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
