package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.spring.properties.ArthasProperties;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionPropertiesPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private ArthasProperties arthasProperties;

    public ArthasExtensionPropertiesPostProcessor(ArthasProperties arthasProperties) {
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


        Set<TraceMethodInfo> traceMethodInfos = arthasProperties.traceMethods();
        for (TraceMethodInfo traceMethodInfo : traceMethodInfos) {

            //注入到容器中
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ArthasProperties.class, () -> arthasProperties);
            registry.registerBeanDefinition(ArthasProperties.class.getName(), beanDefinitionBuilder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
