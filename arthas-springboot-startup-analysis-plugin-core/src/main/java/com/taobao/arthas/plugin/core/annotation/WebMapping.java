package com.taobao.arthas.plugin.core.annotation;

import com.taobao.arthas.plugin.core.condition.OnTurboCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Conditional(OnTurboCondition.class)
public @interface WebMapping {

    String[] value();

    String contentType() default "text/html; charset=UTF-8";

}
