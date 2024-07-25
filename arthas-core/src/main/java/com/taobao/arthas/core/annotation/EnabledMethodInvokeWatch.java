package com.taobao.arthas.core.annotation;

import com.taobao.arthas.core.configuration.ArthasExtensionMethodInvokeImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ArthasExtensionMethodInvokeImportBeanDefinitionRegistrar.class)
public @interface EnabledMethodInvokeWatch {

    MethodInvokeWatch[] value();

}
