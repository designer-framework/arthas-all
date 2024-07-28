package com.taobao.arthas.core.annotation;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodInvokeWatch {

    String value();

    boolean canRetransform() default false;

    Class<? extends SpyInterceptorApi> spyInterceptorApiClass() default SimpleSpyInterceptorApi.class;

}
