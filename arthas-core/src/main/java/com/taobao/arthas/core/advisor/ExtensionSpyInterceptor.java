package com.taobao.arthas.core.advisor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.profiling.api.interceptor.SpyInterceptorExtensionApi;

import java.arthas.SpyAPI;

public class ExtensionSpyInterceptor implements SpyInterceptorExtensionApi {

    /**
     * @see com.taobao.arthas.core.advisor.SpyInterceptors.SpyInterceptor1
     */
    @AtEnter(inline = true)
    public static void atEnter(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args) {
        SpyAPI.atEnter(clazz, methodName, methodDesc, target, args);
    }

    /**
     * @see com.taobao.arthas.core.advisor.SpyInterceptors.SpyInterceptor2
     */
    @AtExit(inline = true)
    public static void atExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Return Object returnObj) {
        SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj);
    }

    /**
     * @see com.taobao.arthas.core.advisor.SpyInterceptors.SpyInterceptor3
     */
    @AtExceptionExit(inline = true)
    public static void atExceptionExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Throwable Throwable throwable) {
        SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable);
    }

}
