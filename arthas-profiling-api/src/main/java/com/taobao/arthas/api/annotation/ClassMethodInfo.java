package com.taobao.arthas.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassMethodInfo {

    String className();

    String methodName();

    String[] methodArgumentTypes();

}
