package com.taobao.arthas.core.annotation;

import com.taobao.arthas.core.configuration.instrument.InstrumentationBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Import({InstrumentationBeanDefinitionRegistrar.class})
public @interface EnabledInstrument {

    Retransform[] value();

}
