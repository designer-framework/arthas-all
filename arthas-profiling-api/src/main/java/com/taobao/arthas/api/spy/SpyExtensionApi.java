package com.taobao.arthas.api.spy;

/**
 * 便于外部拓展
 */
public interface SpyExtensionApi {

    void atEnter(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args);

    void atExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject);

    void atExceptionExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable);

}
