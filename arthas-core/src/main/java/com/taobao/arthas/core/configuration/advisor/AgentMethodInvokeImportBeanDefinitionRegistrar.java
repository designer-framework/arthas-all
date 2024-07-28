package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import com.taobao.arthas.core.properties.AgentMethodTraceProperties;
import lombok.Setter;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Collection;

public class AgentMethodInvokeImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    @Setter
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        //
        if (Boolean.parseBoolean(environment.resolvePlaceholders(BeanDefinitionRegistryUtils.ENABLED))) {
            return;
        }

        annotationMetadata.getAnnotations().stream(EnabledMethodInvokeWatch.class)
                .map(annotation -> Arrays.asList(annotation.getAnnotationArray("value", MethodInvokeWatch.class)))
                .flatMap(Collection::stream)
                .forEach(methodInvokeWatch -> {

                    BeanDefinitionRegistryUtils.registry(registry,
                            new AgentMethodTraceProperties.ClassMethodDesc(methodInvokeWatch.getString("value"), methodInvokeWatch.getBoolean("canRetransform"))
                    );

                });

    }

}
