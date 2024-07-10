package com.taobao.arthas.profiling.api.spy;

public interface SpyExtensionApi {

    void atEnter(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args);

    void atExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject);

    void atExceptionExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable);

}
