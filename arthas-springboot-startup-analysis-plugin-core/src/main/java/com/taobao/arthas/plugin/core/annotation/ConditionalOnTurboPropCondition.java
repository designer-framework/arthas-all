package com.taobao.arthas.plugin.core.annotation;

import com.taobao.arthas.plugin.core.condition.OnTurboCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Conditional(OnTurboCondition.class)
public @interface ConditionalOnTurboPropCondition {

    /**
     * 插件名
     *
     * @return
     */
    String pluginName();

}
