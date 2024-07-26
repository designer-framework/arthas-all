package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import com.taobao.arthas.core.properties.ArthasMethodTraceProperties;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Collection;

public class ArthasExtensionMethodInvokeImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        annotationMetadata.getAnnotations().stream(EnabledMethodInvokeWatch.class)
                .map(annotation -> Arrays.asList(annotation.getAnnotationArray("value", MethodInvokeWatch.class)))
                .flatMap(Collection::stream)
                .forEach(methodInvokeWatch -> {

                    ArthasMethodInvokeRegistryPostProcessor.registry(registry,
                            new ArthasMethodTraceProperties.ClassMethodDesc(methodInvokeWatch.getString("value"), methodInvokeWatch.getBoolean("canRetransform"))
                    );

                });

    }

}
