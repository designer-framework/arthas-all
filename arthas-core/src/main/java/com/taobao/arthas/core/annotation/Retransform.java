package com.taobao.arthas.core.annotation;

import com.taobao.arthas.core.configuration.instrument.InstrumentationBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import({InstrumentationBeanDefinitionRegistrar.class})
public @interface Retransform {

    /**
     * 需要被修改的类
     *
     * @return
     */
    String className();

    /**
     * 目标样
     * <p>
     * {@link com.alibaba.bytekit.agent.inst.Instrument 类上必须添加该注解}
     */
    Class<?> instrumentClass();

}
