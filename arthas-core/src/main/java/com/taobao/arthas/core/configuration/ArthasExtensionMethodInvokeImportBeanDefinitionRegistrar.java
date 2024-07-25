package com.taobao.arthas.core.configuration;

import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import com.taobao.arthas.core.properties.ArthasMethodTraceProperties;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class ArthasExtensionMethodInvokeImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(EnabledMethodInvokeWatch.class.getName(), false)
        );

        for (MethodInvokeWatch methodInvokeWatch : annotationAttributes.getAnnotationArray("value", MethodInvokeWatch.class)) {
            ArthasMethodInvokePostProcessor.registry(registry,
                    new ArthasMethodTraceProperties.ClassMethodDesc(methodInvokeWatch.value(), methodInvokeWatch.canRetransform())
            );
        }
    }

}
