package com.taobao.arthas.plugin.core.annotation;

import com.taobao.arthas.plugin.core.condition.OnTurboCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Conditional(OnTurboCondition.class)
public @interface ConditionalOnTurboCondition {

    String pluginName();

}
