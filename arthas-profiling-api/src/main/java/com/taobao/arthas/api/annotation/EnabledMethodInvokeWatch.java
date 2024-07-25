package com.taobao.arthas.api.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnabledMethodInvokeWatch {

    ClassMethodInfo[] value();

}
